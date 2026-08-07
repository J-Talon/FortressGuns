package me.camm.productions.fortressguns.Util.chunk;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Math.IntTuple2;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChunkTicket {

    private final Construct construct;
    private final Set<IntTuple2> chunks;
    private final Chunk coreChunk;
    private final Entity oldCore;
    private final String worldName;
    private final World world;


    private final ReentrantLock lock;
    final int numChunks;
    private long loadTime;
    AtomicInteger currentLoaded;


    private final Logger logger;


    public ChunkTicket(Set<IntTuple2> chunks, int loaded, Construct construct, Entity oldCore, World world) {
        this.construct = construct;
        this.chunks = chunks;
        numChunks = chunks.size();
        currentLoaded = new AtomicInteger(loaded);
        coreChunk = construct.getInitialChunk();
        this.oldCore = oldCore;
        this.worldName = world.getName();
        this.world = world;
        this.lock = new ReentrantLock();


        logger = FortressGuns.getInstance().getLogger();
    }


    /*
    return: true -> All chunks have loaded, prepare to spawn the construct
            false -> Not all chunks loaded, keep watching the ticket
     */
    public synchronized boolean onLoad() {

        if  (currentLoaded.updateAndGet((value) -> Math.min(value + 1, numChunks)) > numChunks) {
            this.loadTime = System.currentTimeMillis();
            logger.log(Level.INFO, getUUID() +" loaded, now at "+currentLoaded.get() +" / "+numChunks);

            return true;

        }
        return false;

    }


    /*
    return: true -> Not all chunks have unloaded, keep watching the ticket
            false -> All chunks in the ticket have unloaded, remove the ticket
    */
    public synchronized boolean onUnload() {

        lock.lock();
        int val = currentLoaded.updateAndGet((value) -> Math.max(0, value - 1));

        logger.log(Level.INFO, getUUID() +" unloaded, now at "+val +" / "+numChunks);

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

        World world = oldCore.getWorld();
        for (IntTuple2 tup: chunks) {

            if (!ChunkUtils.isChunkEntityTicking(world, tup.getA(), tup.getB())) {
                lock.unlock();
                return false;
            }

//            Chunk c = world.getChunkAt(tup.getA(), tup.getB());
//
//
//            if (! c.isEntitiesLoaded()) {
//                lock.unlock();
//                return false;
//            }
        }

        lock.unlock();
        return true;
    }


    public void markLoadTime() {
        this.loadTime = System.currentTimeMillis();
    }


    public synchronized boolean canFinish() {
        return (System.currentTimeMillis() - loadTime >= 1000);
    }


    public void finish() {
        oldCore.remove();
        construct.spawn();
    }


    public Set<IntTuple2> getChunks() {
        return chunks;
    }

    public Construct getConstruct(){
        return construct;
    }

    public UUID getUUID() {
        return construct.getUUID();
    }

    public String getWorldName() {
        return worldName;
    }

    public int getCurrentLoaded() {
        return currentLoaded.get();
    }

    public int getTotalChunks() {
        return numChunks;
    }

    public boolean isLoaded() {
        return construct.getOccupiedChunks().stream().filter(tup -> world.isChunkLoaded(tup.getA(), tup.getB())).count() >= numChunks;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getUUID().toString() + " @ " + worldName + ": ");
        for (IntTuple2 c: chunks) {

            String mod = "";
            if (world.isChunkLoaded(c.getA(), c.getB())) {
                mod += "l";

                Chunk chunk = world.getChunkAt(c.getA(), c.getB());
                if (chunk.isEntitiesLoaded())
                    mod += "e";
            }
            s.append(" ").append(mod).append(c);
        }
        return s.toString();

    }
}
