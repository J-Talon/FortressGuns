package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructFactory;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;


import java.util.*;

/**
 * @author CAMM
 */


   /*
   In unloaded state, only core should remain with the PDC
   When construct is loaded from unloaded state, remove the old core.

   [1]
   if required chunks are loaded, spawn, else add a chunk ticket to handler
   which spawns when all chunks loaded.

   then add artillery core to shutdown manager.

   on chunk unload/plugin shutdown
    - add PDC, unload artillery if it is loaded

    on chunk load/entity load
    - see [1]


   the load function is basically the spawn() function

   handler.addAll(calculatedChunks)


    */


//consider also doing the portal events here to prevent portals causing dislocation
public class ChunkLoader implements Listener
{
    private final static Map<String, WorldTicketManager> pieces;
    private final static Map<String, WorldTicketManager> activeConstructs;

    static {
      pieces = new HashMap<>();
      activeConstructs = new HashMap<>();
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        net.minecraft.world.entity.Entity nms = ((CraftEntity)event.getEntity()).getHandle();
        if (nms instanceof Component || nms instanceof ProjectileFG) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onEntityLoad(EntitiesLoadEvent event) {

        String worldName = event.getWorld().getName();
        updateWorldEntries(worldName);

        List<Entity> entities = event.getEntities();
        NamespacedKey key = new NamespacedKey(FortressGuns.getInstance(), ConstructFactory.getKey());
        for (Entity entity: entities) {
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            Construct struct = deserializeConstruct(entity.getLocation(),pdc, key);
            if (struct == null)
                continue;

            Set<Chunk> requiredChunks = struct.getOccupiedChunks();

            int loaded = (int)requiredChunks.stream().filter(Chunk::isLoaded).count();
            if (loaded == requiredChunks.size()) {
                activeConstructs.get(worldName).addTicket(createTicket(requiredChunks,struct, event.getWorld()));
                struct.spawn();
            }
            else {
                addLoadingTicket(requiredChunks,event.getWorld(),struct);
            }
        }
    }


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        World world = event.getWorld();
        Chunk chunk = event.getChunk();

        String name = world.getName();

        updateWorldEntries(name);

        Set<ChunkTicket> tickets = pieces.get(world.getName()).update(chunk.getX(), chunk.getZ(),true);
        if (tickets == null)
            return;

        for (ChunkTicket ticket: tickets) {
            Construct struct = ticket.getConstruct();

            if (struct.getOccupiedChunks().stream().allMatch(Chunk::isLoaded)) {
                activeConstructs.get(name).addTicket(ticket);
                struct.spawn();
            }
            else {
                throw new IllegalStateException("Not all chunks were loaded for construct type "+struct.getType()+" at chunk "+chunk.getX()+", "+chunk.getZ() +" in world "+world.getName());
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        World world = event.getWorld();

        String worldName = world.getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        pieces.get(worldName).update(x, z,false);


        Set<ChunkTicket> tickets = activeConstructs.get(worldName).unloadReturnIntermediate(x,z);

        if (tickets == null)
            return;

        for (ChunkTicket ticket: tickets) {
            Construct struct = ticket.getConstruct();

            //well this isn't enough - what if not ALL of the required chunks unload?
            //then this really should be added to the tickets instead huh?
            int currentLoaded = (int)struct.getOccupiedChunks().stream().filter(Chunk::isLoaded).count();
            if (currentLoaded == 0)
                struct.unload();
            else {
                addLoadingTicket(ticket, world);
            }
        }

        /*
        The dilemma

        we need to target constructs on unload to update the rotation data, but we cannot access it easy without
        using nms.

         -> if you use nms this won't be version independent. You'll need to use a translator to determine
         whether something is a construct

         -> the other option is to use some other way to determine whether it is a construct without using
         nms
         : options:
           - pdc < could work but don't forget the surrounding chunks
           - a second map<int, map<int, set<cons>>> to track active cons < also could work but the memory...
           - "lazy" data update on cons -> update on finish event < desync in some situations
         */
    }


    public ChunkTicket createTicket(Set<Chunk> chunks, Construct construct, World world) {
        Set<Tuple2<Integer, Integer>> coords = new HashSet<>();
        int loaded = 0;
        for (Chunk chunk: chunks) {
            coords.add(new Tuple2<>(chunk.getX(),chunk.getZ()));
            if (chunk.isLoaded())
                loaded ++;
        }

        return new ChunkTicket(coords,loaded,world,construct);
    }

    public @Nullable Construct deserializeConstruct(Location loc, PersistentDataContainer pdc, NamespacedKey key) {

        if (!pdc.has(key, PersistentDataType.INTEGER_ARRAY)) {
            return null;
        }

        int[] data = pdc.get(key, PersistentDataType.INTEGER_ARRAY);

        if (data == null || data.length == 0)
            return null;

        int[] copy = new int[data.length-1];
        System.arraycopy(data,1,copy,0,copy.length);

        ConstructType type = ConstructFactory.deserializeType(data[0]);
        if (type == null)
            return null;

        ConstructFactory<?> factory = type.getFactory();
        return factory.create(loc,copy);

    }

    //ticket for partially loaded constructs
    public void addLoadingTicket(Set<Chunk> chunks, World world, Construct struct) {
        ChunkTicket ticket = createTicket(chunks, struct, world);
        WorldTicketManager manager = pieces.get(world.getName());
        manager.addTicket(ticket);
    }

    //ticket for partially loaded constructs
    public void addLoadingTicket(ChunkTicket ticket, World world) {
        WorldTicketManager manager = pieces.get(world.getName());
        manager.addTicket(ticket);
    }


    public void updateWorldEntries(String worldName) {
        if (!pieces.containsKey(worldName)) {
            pieces.put(worldName, new WorldTicketManager());
        }

        if (!activeConstructs.containsKey(worldName)) {
            activeConstructs.put(worldName, new WorldTicketManager());
        }

    }



//    @EventHandler
//    public void onChunkLoad(ChunkLoadEvent event){
//
//        Chunk chunk = event.getChunk();
//        int x, z;
//        x = chunk.getX();
//        z = chunk.getZ();
//
//        if (!pieces.contains(x,z))
//            return;
//
//        Map<String, Set<Construct>> map = pieces.get(x,z);
//        String name = event.getWorld().getName();
//
//        if (!map.containsKey(name))
//            return;
//
//        Set<Construct> set = new HashSet<>(map.get(name));
//
//        for (Construct next : set) {
//            Set<Chunk> loaders = next.getOccupiedChunks();
//            boolean loaded = loaders.stream().allMatch(Chunk::isLoaded);
//            if (next.isInvalid()) {
//                remove(loaders, next);
//                continue;
//            }
//
//            if (loaded) {
//                next.spawn();
//                next.setChunkLoaded(true);
//            }
//
//        }
//    }




//
//    public void add(Chunk chunk, Construct construct){
//
//        int x = chunk.getX();
//        int z = chunk.getZ();
//
//        Set<Construct> set;
//        Map<String, Set<Construct>> map;
//        String name = chunk.getWorld().getName();
//        if (pieces.contains(x,z)) {
//
//
//            map = pieces.get(x,z);
//            if (! map.containsKey(name)) {
//                set = new HashSet<>();
//                set.add(construct);
//                map.put(name, set);
//            }
//            else {
//                set = map.get(name);
//                set.add(construct);
//            }
//        }
//        else
//        {
//            set = new HashSet<>();
//            map = new ConcurrentHashMap<>();
//            set.add(construct);
//            map.put(name,set);
//            pieces.put(x,z,map);
//        }
//
//    }
//
//    //remove an artillery from the set
//    public void remove(Set<Chunk> chunks, Construct construct){
//
//
//        for (Chunk chunk : chunks) {
//            int x = chunk.getX();
//            int z = chunk.getZ();
//
//            if (!pieces.contains(x, z))
//                continue;
//
//            Map<String, Set<Construct>> map = pieces.get(x, z);
//
//            String name = chunk.getWorld().getName();
//
//            if (!map.containsKey(name))
//                continue;
//
//            Set<Construct> set = map.get(name);
//            set.remove(construct);
//            construct.setChunkLoaded(false);
//        }
//
//    }
//
//
//
//    public List<Construct> getEntries() {
//        List<Construct> list = new LinkedList<>();
//        Map<Integer,Map<Integer, Map<String, Set<Construct>>>> xMap = pieces.rowMap();
//        for (int x: xMap.keySet()) {
//
//            Map<Integer,Map<String, Set<Construct>>> zMap = xMap.get(x);
//
//            for (int z: zMap.keySet()) {
//                Map<String, Set<Construct>> chunkWorldMap = zMap.get(z);
//
//                for (String worldName: chunkWorldMap.keySet()) {
//
//                    Set<Construct> constructsInChunk = chunkWorldMap.get(worldName);
//                    list.addAll(constructsInChunk);
//
//                }
//            }
//
//        }
//
//        return list;
//    }


//
//    @EventHandler
//    public void onChunkUnload(ChunkUnloadEvent event){
//
//        Chunk chunk = event.getChunk();
//        int x = chunk.getX();
//        int z = chunk.getZ();
//        String name = chunk.getWorld().getName();
//
//        Entity[] entities = chunk.getEntities();
//
//        for (Entity e: entities) {
//            net.minecraft.world.entity.Entity nms = ((CraftEntity)e).getHandle();
//            if (nms instanceof ProjectileExplosive) {
//                ((ProjectileExplosive)nms).explode(null);
//            }
//
//            if (nms instanceof ProjectileFG) {
//                ((ProjectileFG)nms).remove();
//            }
//        }
//
//        if (!pieces.contains(x,z)) {
//            return;
//        }
//
//        Map<String, Set<Construct>> map = pieces.get(x,z);
//
//        if (!map.containsKey(name)) {
//            return;
//        }
//
//        //prevent the concurrent mod exception with sets
//        Set<Construct> set = new HashSet<>(map.get(name));
//
//
//        for (Construct next : set) {
//            if (next.isInvalid()) {
//                remove(next.getOccupiedChunks(), next);
//                continue;
//            }
//
//            next.destroy(false, false);
//            next.setChunkLoaded(false);
//        }
//    }
}
