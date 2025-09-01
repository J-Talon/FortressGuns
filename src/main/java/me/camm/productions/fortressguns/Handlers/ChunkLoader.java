package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructFactory;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.FactorySerialization;
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


    static {
      pieces = new HashMap<>();
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        net.minecraft.world.entity.Entity nms = ((CraftEntity)event.getEntity()).getHandle();
        if (nms instanceof ComponentAS || nms instanceof ProjectileFG) {
            event.setCancelled(true);
        }
    }



    //entities loaded = false
    //chunk loaded = true
    @EventHandler
    public synchronized void onChunkLoad(ChunkLoadEvent event) {
        World world = event.getWorld();
        Chunk chunk = event.getChunk();
        String name = world.getName();

        int x = chunk.getX();
        int z = chunk.getZ();


        updateWorldEntries(name);


        Set<ChunkTicket> tickets = managerUpdate(world.getName(),x,z,true);
        if (tickets != null) {
            for (ChunkTicket ticket : tickets) {
                //System.out.println("update finalize load: " + ticket.chunkString());
                ticket.onFinalizeLoad();
            }
        }


        NamespacedKey key = new NamespacedKey(FortressGuns.getInstance(), FactorySerialization.getKey());

        //discover
        for (Entity entity: chunk.getEntities()) {

            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            if (!pdc.has(key, PersistentDataType.INTEGER_ARRAY)) {
                continue;
            }

            Construct struct = deserializeConstruct(entity.getLocation(),pdc, key);
            if (struct == null)
                continue;

            struct.recalculateOccupiedChunks();
            Set<Chunk> loadedChunks = struct.getOccupiedChunks();

            int loaded = (int)loadedChunks.stream().filter(Chunk::isLoaded).count();
            if (loaded == loadedChunks.size()) {
               // System.out.println("discover: spawn "+x+" "+z);
                struct.spawn();
                entity.remove();
            }
            else {
                //0 because the chunk is already loaded
             ChunkTicket ticket = createTicket(loadedChunks,struct,world,entity,0);
             addLoadingTicket(ticket, world);
            // System.out.println("discover - add ticket: "+ticket.chunkString());
            }
        }
    }


    //entities loaded = true
    //chunk loaded = true
    @EventHandler
    public synchronized void onChunkUnload(ChunkUnloadEvent event) {

        Chunk chunk = event.getChunk();
        World world = event.getWorld();

        int x = chunk.getX();
        int z = chunk.getZ();


       managerUpdate(world.getName(), x, z, false);
//        if (tickets != null) {
//            for (ChunkTicket ticket : tickets) {
//                System.out.println("unload update: " + ticket.chunkString());
//            }
//        }

        NamespacedKey key = new NamespacedKey(FortressGuns.getInstance(),FactorySerialization.getKey());

        //discover on unload
        for (Entity entity: chunk.getEntities()) {

            //will require a translator
            net.minecraft.world.entity.Entity nms = ((CraftEntity)entity).getHandle();

            if (!(nms instanceof ComponentAS component))
                continue;

            if (entity.getPersistentDataContainer().has(key,PersistentDataType.INTEGER_ARRAY)) {
                continue;
            }

            Construct struct = component.getBody();
            struct.recalculateOccupiedChunks();
            Set<Chunk> chunks = struct.getOccupiedChunks();
            //System.out.println("chunk size: "+chunks.size());

            Entity pivot = struct.getCoreEntity();

            struct.unload();

            if (chunks.size() == 1) {
                continue;
            }

            //-1 because this chunk will be unloaded.
            ChunkTicket ticket = createTicket(chunks, struct, world, pivot, -1);

           // System.out.println("adding loading ticket for: "+ticket.chunkString());
            addLoadingTicket(ticket,world);
        }

    }


    public ChunkTicket createTicket(Set<Chunk> chunks, Construct construct, World world, Entity pdc, int offset) {
        Set<Tuple2<Integer, Integer>> coords = new HashSet<>();
        int loaded = 0;
        for (Chunk chunk: chunks) {
            coords.add(new Tuple2<>(chunk.getX(), chunk.getZ()));
            if (chunk.isLoaded()) {
                loaded ++;
            }
        }
        loaded += offset;

        return new ChunkTicket(coords,loaded,world,construct, pdc);
    }

    public synchronized @Nullable Set<ChunkTicket> managerUpdate(String worldName, int x, int z, boolean onload) {
        WorldTicketManager manager = pieces.getOrDefault(worldName, null);
        if (manager == null)
            return null;

        return manager.update(x,z, onload);
    }


    public @Nullable Construct deserializeConstruct(Location loc, PersistentDataContainer pdc, NamespacedKey key) {

        int[] data = pdc.get(key, PersistentDataType.INTEGER_ARRAY);

        if (data == null || data.length == 0)
            return null;

        ConstructType type = FactorySerialization.deserializeType(data[0]);
        if (type == null)
            return null;

        ConstructFactory<?> factory = type.getFactory();
        //System.out.println("Deserializing cons");
        return factory.create(loc,data);

    }

    //ticket for partially loaded constructs
    public void addLoadingTicket(ChunkTicket ticket, World world) {
        WorldTicketManager manager = pieces.get(world.getName());
        manager.addTicket(ticket);
    }



    public void updateWorldEntries(String worldName) {
        if (!pieces.containsKey(worldName)) {
            pieces.put(worldName, new WorldTicketManager(worldName));
        }
    }

}
