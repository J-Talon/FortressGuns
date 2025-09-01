package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ChunkTicket {

    private final Construct construct;
    Set<Tuple2<Integer, Integer>> chunks;
    final World world;
    final Chunk coreChunk;
    private final Entity pdc;


    final int numChunks;
    AtomicInteger currentLoaded;


    public ChunkTicket(Set<Tuple2<Integer, Integer>> chunks, int loaded, World world, Construct construct, Entity pdc) {
        this.construct = construct;
        this.chunks = chunks;
        this.world = world;
        numChunks = chunks.size();
        currentLoaded = new AtomicInteger(loaded);
        coreChunk = construct.getInitialChunk();
        this.pdc = pdc;
    }

    //return: true -> spawn construct
    //        false -> do not spawn yet
    public synchronized boolean onLoad() {

        currentLoaded.updateAndGet((value) -> {
            return Math.min(value, numChunks);
        });
        return currentLoaded.incrementAndGet() >= numChunks;
    }


    //return: true -> keep ticket active
    //        false -> remove ticket
    public synchronized boolean onUnload() {
        if (!coreChunk.isLoaded()) {
            currentLoaded.updateAndGet((value) -> {
                return Math.max(0, value - 1);
            });
            return false;
        }
        else
            return currentLoaded.decrementAndGet() > 0;
    }

    public synchronized boolean allUnloaded() {
        return currentLoaded.get() >= numChunks;
    }


    public void onFinalizeLoad() {
        pdc.remove();
        construct.spawn();
    }


    public Set<Tuple2<Integer, Integer>> getChunks() {
        return chunks;
    }

    public Construct getConstruct(){
        return construct;
    }

    public String chunkString() {
        String values = "|";
        for (Tuple2<Integer, Integer> tup: chunks) {
            values += "("+tup.getA()+", "+tup.getB()+")";
        }
        values += "|";
        return values;
    }

    public UUID getUUID() {
        return pdc.getUniqueId();
    }
}
