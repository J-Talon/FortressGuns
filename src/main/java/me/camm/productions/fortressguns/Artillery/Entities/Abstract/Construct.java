package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Util.Tuple2;
import me.camm.productions.fortressguns.Util.chunk.ChunkLoader;
import org.bukkit.Chunk;

import org.bukkit.Location;
import org.bukkit.entity.Entity;


import java.util.Set;
import java.util.UUID;

public abstract class Construct {

    protected final static double LARGE_BLOCK_LENGTH = 0.6;
    protected final static double SMALL_BLOCK_LENGTH = 0.4;

    private final UUID id;

    public Construct() {
        this.id = UUID.randomUUID();
    }




    public abstract boolean spawn();

    public abstract void setChunkLoaded(boolean loaded);

    public abstract boolean chunkLoaded();

    public abstract Set<Tuple2<Integer, Integer>> getOccupiedChunks();

    public abstract Chunk getCurrentChunk();

    public abstract Chunk getInitialChunk();

    public abstract Location getInitialLoc();

    public abstract void calculateOccupiedChunks();

    public abstract Entity getCoreEntity();

   public void destroy(boolean drop, boolean explode) {
       ChunkLoader.removeActivePiece(this);
   }

    public abstract double getHealth();

    public abstract void setHealth(double health);

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
  */
   public abstract void unload();

   public abstract boolean isInvalid();

   public abstract ConstructType getType();


   public UUID getUUID() {
       return id;
   }



}
