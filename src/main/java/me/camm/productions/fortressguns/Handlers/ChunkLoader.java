package me.camm.productions.fortressguns.Handlers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.util.EulerAngle;


import java.util.*;

/**
 * @author CAMM
 */
public class ChunkLoader implements Listener
{

    private final static Table<Integer, Integer, Set<Artillery>> pieces;

    static {
      pieces = HashBasedTable.create();
    }


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){

        Chunk chunk = event.getChunk();
        int x, z;
        x = chunk.getX();
        z = chunk.getZ();

        if (!pieces.contains(x,z))
            return;

            Set<Artillery> set = pieces.get(x,z);
            Iterator<Artillery> iter = set.iterator();
            while (iter.hasNext()) {

                Artillery next = iter.next();
                Set<Chunk> loaders = next.getLoaders();
                boolean loaded = loaders.stream().allMatch(Chunk::isLoaded);

                if (loaded) {
                    next.spawn();
                    next.setChunkLoaded(true);
                    EulerAngle aim = next.getAim();
                    next.pivot(aim.getY(),aim.getX());
                }
            }
    }

    public void add(Chunk chunk, Artillery artillery){

        int x = chunk.getX();
        int z = chunk.getZ();

        Set<Artillery> set;
        if (pieces.contains(x,z)) {
            set = pieces.get(x,z);
            set.add(artillery);
        }
        else
        {
            set = new HashSet<>();
            set.add(artillery);
            pieces.put(x,z,set);
        }

    }

    public void remove(Set<Chunk> chunks, Artillery artillery){


        Iterator<Chunk> iter = chunks.iterator();
        while (iter.hasNext()) {
            Chunk chunk = iter.next();
            int x = chunk.getX();
            int z = chunk.getZ();

            if (!pieces.contains(x, z))
                continue;

            Set<Artillery> set = pieces.get(x, z);
            set.remove(artillery);
        }

    }


    //should be good
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChunkUnload(ChunkUnloadEvent event){

        Chunk chunk = event.getChunk();
        int x = chunk.getX();
        int z = chunk.getZ();

        if (!pieces.contains(x,z))
            return;

        Set<Artillery> set = pieces.get(x,z);
        Iterator<Artillery> iter = set.iterator();
        while (iter.hasNext()) {
            Artillery next = iter.next();
            next.unload(false, false);
            next.setChunkLoaded(false);
        }
    }
}
