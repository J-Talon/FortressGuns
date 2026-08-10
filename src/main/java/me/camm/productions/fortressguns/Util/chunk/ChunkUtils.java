package me.camm.productions.fortressguns.Util.chunk;



//See: PlayerChunk.class
// There are chunk states in there

/*
Chunk.isLoaded() calls the world which calls the world provider
So chunk.isLoaded() will actually load the chunk if getChunk() is called


CraftWorld --> return this.world.getChunkProvider().isChunkLoaded(x, z);

ChunkProviderServer -->

public boolean isChunkLoaded(int chunkX, int chunkZ) {
        PlayerChunk chunk = this.a.getUpdatingChunk(ChunkCoordIntPair.pair(chunkX, chunkZ));
        if (chunk == null) {
            return false;
        } else {
            return chunk.getFullChunk() != null;
        }
    }

   this.a is public


   Note however that:

       public Chunk getFullChunk() {
        return !getChunkState(this.o).isAtLeast(PlayerChunk.State.b) ? null : this.getFullChunkUnchecked();
    }

    where state.b isn't entity ticking state
    I'm pretty sure state.d is ticking state



 */


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.Math.IntTuple2;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.world.level.ChunkCoordIntPair;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;

import java.util.Set;

public class ChunkUtils {


    //This shouldn't load the chunk (I think)
    //returns whether a chunk is in the entity ticking state
    public static boolean isChunkEntityTicking(World world, int x, int z) {
        net.minecraft.world.level.World nmsWorld = ((CraftWorld) world).getHandle();
        ChunkProviderServer provider = (ChunkProviderServer)nmsWorld.getChunkProvider();
        long pair = ChunkCoordIntPair.pair(x,z);

        PlayerChunk playerChunk = provider.a.k.get(pair);
        if (playerChunk == null) return false;

        return PlayerChunk.getChunkState(playerChunk.o).isAtLeast(PlayerChunk.State.d);

    }


    public static ChunkTicket createTicket(Set<IntTuple2> chunks, Construct construct, Entity pdc, int offset) {
        World world = pdc.getWorld();

        int loaded = 0;
        for (IntTuple2 tup: chunks) {
            if (world.isChunkLoaded(tup.getA(), tup.getB()))
                loaded ++;
        }

        loaded += offset;
        return new ChunkTicket(chunks,loaded,construct, pdc, world);
    }


    public static long chunkId(int x, int z) {
        return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
    }




}
