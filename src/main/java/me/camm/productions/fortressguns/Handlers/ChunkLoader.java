package me.camm.productions.fortressguns.Handlers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import jdk.javadoc.internal.doclets.toolkit.taglets.snippet.Style;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Projectiles.ArtilleryProjectile;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;



import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CAMM
 */



//consider also doing the portal events here to prevent portals causing dislocation
public class ChunkLoader implements Listener
{
    private final static Table<Integer, Integer, Map<String, Set<Construct>>> pieces;

    static {
      pieces = HashBasedTable.create();
    }



    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        net.minecraft.world.entity.Entity nms = ((CraftEntity)event.getEntity()).getHandle();
        if (nms instanceof Component) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){

        Chunk chunk = event.getChunk();
        int x, z;
        x = chunk.getX();
        z = chunk.getZ();

        if (!pieces.contains(x,z))
            return;

        Map<String, Set<Construct>> map = pieces.get(x,z);
        String name = event.getWorld().getName();

        if (!map.containsKey(name))
            return;

        Set<Construct> set = map.get(name);


        for (Construct next : set) {

            Set<Chunk> loaders = next.getOccupiedChunks();
            boolean loaded = loaders.stream().allMatch(Chunk::isLoaded);
            if (next.isInvalid()) {
                remove(loaders, next);
                continue;
            }

            if (loaded) {
                next.spawn();
                next.setChunkLoaded(true);
            }
        }
    }

    public void add(Chunk chunk, Construct construct){

        int x = chunk.getX();
        int z = chunk.getZ();

        Set<Construct> set;
        Map<String, Set<Construct>> map;
        String name = chunk.getWorld().getName();
        if (pieces.contains(x,z)) {


            map = pieces.get(x,z);
            if (! map.containsKey(name)) {
                set = new HashSet<>();
                set.add(construct);
                map.put(name, set);
            }
            else {
                set = map.get(name);
                set.add(construct);
            }
        }
        else
        {
            set = new HashSet<>();
            map = new ConcurrentHashMap<>();
            set.add(construct);
            map.put(name,set);
            pieces.put(x,z,map);
        }

    }

    //remove an artillery from the set
    public void remove(Set<Chunk> chunks, Construct construct){


        for (Chunk chunk : chunks) {
            int x = chunk.getX();
            int z = chunk.getZ();

            if (!pieces.contains(x, z))
                continue;

            Map<String, Set<Construct>> map = pieces.get(x, z);

            String name = chunk.getWorld().getName();

            if (!map.containsKey(name))
                continue;

            Set<Construct> set = map.get(name);
            set.remove(construct);
            construct.setChunkLoaded(false);
        }

    }



    public List<Construct> getEntries() {
        List<Construct> list = new LinkedList<>();
        Map<Integer,Map<Integer, Map<String, Set<Construct>>>> xMap = pieces.rowMap();
        for (int x: xMap.keySet()) {

            Map<Integer,Map<String, Set<Construct>>> zMap = xMap.get(x);

            for (int z: zMap.keySet()) {
                Map<String, Set<Construct>> chunkWorldMap = zMap.get(z);

                for (String worldName: chunkWorldMap.keySet()) {

                    Set<Construct> constructsInChunk = chunkWorldMap.get(worldName);
                    list.addAll(constructsInChunk);

                }
            }

        }

        return list;
    }



    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){

        Chunk chunk = event.getChunk();
        int x = chunk.getX();
        int z = chunk.getZ();
        String name = chunk.getWorld().getName();

        Entity[] entities = chunk.getEntities();

        for (Entity e: entities) {
            net.minecraft.world.entity.Entity nms = ((CraftEntity)e).getHandle();
            if (nms instanceof ArtilleryProjectile) {
                ((ArtilleryProjectile)nms).preHit(null);
            }
        }

        if (!pieces.contains(x,z)) {
            return;
        }

        Map<String, Set<Construct>> map = pieces.get(x,z);

        if (!map.containsKey(name)) {
            return;
        }

        //prevent the concurrent mod exception with sets
        Set<Construct> set = new HashSet<>(map.get(name));


        for (Construct next : set) {
            if (next.isInvalid()) {
                remove(next.getOccupiedChunks(), next);
                continue;
            }

            next.unload(false, false);
            next.setChunkLoaded(false);
        }
    }
}
