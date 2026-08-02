package me.camm.productions.fortressguns.Util.chunk;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ChunkTicket {

    private final Construct construct;
    private final Set<Tuple2<Integer, Integer>> chunks;
    private final Chunk coreChunk;
    private final Entity oldCore;


    private final ReentrantLock lock;
    final int numChunks;
    private long loadTime;
    AtomicInteger currentLoaded;


    public ChunkTicket(Set<Tuple2<Integer, Integer>> chunks, int loaded, Construct construct, Entity oldCore) {
        this.construct = construct;
        this.chunks = chunks;
        numChunks = chunks.size();
        currentLoaded = new AtomicInteger(loaded);
        coreChunk = construct.getCurrentChunk();
        this.oldCore = oldCore;
        this.lock = new ReentrantLock();
    }


    /*
    return: true -> All chunks have loaded, prepare to spawn the construct
            false -> Not all chunks loaded, keep watching the ticket
     */
    public synchronized boolean onLoad() {
        return currentLoaded.updateAndGet((value) -> {
            return Math.min(value + 1, numChunks);
        }) > numChunks;

    }


    /*
    return: true -> Not all chunks have unloaded, keep watching the ticket
            false -> All chunks in the ticket have unloaded, remove the ticket
    */
    public synchronized boolean onUnload() {

        lock.lock();
        int val = currentLoaded.updateAndGet((value) -> {
            return Math.max(0, value - 1);
        });

        if (!coreChunk.isLoaded()) {
            lock.unlock();
            return false;
        }

        lock.unlock();
        return val > 0;
    }


    /*
    Return whether it is safe to spawn the construct
     */
    public synchronized boolean isAssembled() {
        lock.lock();

        if (!(currentLoaded.get() >= numChunks)) {
            lock.unlock();
            return false;
        }

        for (Tuple2<Integer, Integer> tup: chunks) {
            Chunk c = oldCore.getWorld().getChunkAt(tup.getA(), tup.getB());
            if (!(c.isLoaded() && c.isEntitiesLoaded())) {
                lock.unlock();
                return false;
            }
        }
        lock.unlock();
        return true;
    }


    public void finish() {
        oldCore.remove();
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

    public boolean isLoaded() {
        return construct.getOccupiedChunks().stream().filter(Chunk::isLoaded).count() >= numChunks;
    }
}
