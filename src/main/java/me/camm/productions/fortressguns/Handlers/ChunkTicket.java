package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.World;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ChunkTicket {

    private Construct construct;
    Set<Tuple2<Integer, Integer>> chunks;
    final World world;

    final int numChunks;
    AtomicInteger currentLoaded;


    public ChunkTicket(Set<Tuple2<Integer, Integer>> chunks, int loaded, World world, Construct construct) {
        this.construct = construct;
        this.chunks = chunks;
        this.world = world;
        numChunks = chunks.size();
        currentLoaded = new AtomicInteger(loaded);
    }

    //return: true -> spawn construct
    //        false -> do not spawn yet
    public synchronized boolean onLoad() {
        return currentLoaded.getAndDecrement() <= 0;
    }


    //return: true -> keep ticket active
    //        false -> remove ticket
    public synchronized boolean onUnload() {
        return currentLoaded.getAndIncrement() < numChunks;
    }

    public synchronized boolean allUnloaded() {
        return currentLoaded.get() <= 0;
    }


    public Set<Tuple2<Integer, Integer>> getChunks() {
        return chunks;
    }

    public Construct getConstruct(){
        return construct;
    }



}
