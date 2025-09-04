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
    private final Set<Tuple2<Integer, Integer>> chunks;
    private final Chunk coreChunk;
    private final Entity pdc;


    final int numChunks;
    AtomicInteger currentLoaded;


    public ChunkTicket(Set<Tuple2<Integer, Integer>> chunks, int loaded, Construct construct, Entity pdc) {
        this.construct = construct;
        this.chunks = chunks;
        numChunks = chunks.size();
        currentLoaded = new AtomicInteger(loaded);
        coreChunk = construct.getInitialChunk();
        this.pdc = pdc;
    }
/// //todo
    //return: true -> spawn construct
    //        false -> do not spawn yet
    public synchronized boolean onLoad() {
        return currentLoaded.updateAndGet((value) -> {
            return Math.min(value + 1, numChunks);
        }) > numChunks;

    }


    //return: true -> keep ticket active
    //        false -> remove ticket
    public synchronized boolean onUnload() {

        int val = currentLoaded.updateAndGet((value) -> {
            return Math.max(0, value - 1);
        });

        if (!coreChunk.isLoaded()) {
            return false;
        }
        return val > 0;
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

    public UUID getUUID() {
        return construct.getUUID();
    }

    public int getCurrentValue() {
        return currentLoaded.get();
    }

    public String chunkString() {
        String values = "|";
        for (Tuple2<Integer, Integer> tup: chunks) {
            values += "("+tup.getA()+", "+tup.getB()+")";
        }
        values += "|";
        return values;
    }

    private void update(int src) {
        System.out.println("=>=");
        System.out.println("src: "+src);
        System.out.println("chunk ticket update: "+pdc.getUniqueId() +" "+pdc.getLocation());
        System.out.println("coords:"+chunkString());
        System.out.println("val: "+getCurrentValue());
        System.out.println("=<=");
    }
}
