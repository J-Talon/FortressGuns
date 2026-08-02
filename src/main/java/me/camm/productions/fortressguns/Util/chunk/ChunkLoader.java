package me.camm.productions.fortressguns.Util.chunk;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.Util.Serialization.FactorySerialization;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.Chunk;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;


import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author CAMM
 *
 */

public class ChunkLoader implements Listener {


    private final static Map<String, WorldTicketManager> pieces;


    private final static Set<Construct> activePieces;
    private final static Set<ChunkTicket> assembledTickets;
    private static ChunkLoader loader = null;
    private final NamespacedKey key;


    private final ReentrantLock lock;
    private final BukkitTask task;

    static {
      pieces = new HashMap<>();
      activePieces = new HashSet<>();
      assembledTickets = new HashSet<>();
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        net.minecraft.world.entity.Entity nms = ((CraftEntity)event.getEntity()).getHandle();
        if (nms instanceof ComponentAS || nms instanceof ProjectileFG) {
            event.setCancelled(true);
        }
    }


    private ChunkLoader() {
        this.key = new NamespacedKey(FortressGuns.getInstance(), FactorySerialization.getKey());
        this.lock = new ReentrantLock();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {

                if (!FortressGuns.getInstance().isEnabled())
                    cancel();

                ENTER_CHUNKS:
                {
                    if (assembledTickets.isEmpty())
                        break ENTER_CHUNKS;

                    tick();
                }
            }
        };

        task = runnable.runTaskTimer(FortressGuns.getInstance(), 0,20);
    }

    public static ChunkLoader getInstance() {
        if (loader == null)
            loader = new ChunkLoader();
        return loader;
    }


    //entities loaded = false
    //chunk loaded = true
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

        lock.lock();

        World world = event.getWorld();
        Chunk chunk = event.getChunk();
        String name = world.getName();

        int x = chunk.getX();
        int z = chunk.getZ();

        updateTrackedWorlds(name);
        Set<ChunkTicket> tickets = managerUpdate(world.getName(),x,z,true);

        if (tickets != null)
            assembledTickets.addAll(tickets);
        discoverConstructs(chunk);

        lock.unlock();
    }


    private void discoverConstructs(Chunk chunk) {

        for (Entity e: chunk.getEntities()) {
            PersistentDataContainer pdc = e.getPersistentDataContainer();
            if (!pdc.has(key, PersistentDataType.INTEGER_ARRAY))
                continue;

            Construct struct = FactorySerialization.deserializeConstruct(e.getLocation(),pdc, key);
            if (struct == null)
                continue;

            struct.calculateOccupiedChunks();
            Set<Chunk> loadedChunks = struct.getOccupiedChunks();

            int loaded = (int)loadedChunks.stream().filter(Chunk::isLoaded).count();
            ChunkTicket ticket;
            if (loaded >= loadedChunks.size()) {
                ticket = createTicket(loadedChunks, struct, e, 0);
                assembledTickets.add(ticket);
            }
            else {
                ticket = createTicket(loadedChunks, struct, e, 0);
                addLoadingTicket(ticket, chunk.getWorld());
            }
        }
    }


    //-----------------unloading logic-----------------------------


    //entities loaded = true
    //chunk loaded = true
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {

        lock.lock();

        Chunk chunk = event.getChunk();
        World world = event.getWorld();

        int x = chunk.getX();
        int z = chunk.getZ();

       managerUpdate(world.getName(), x, z, false);
       shelveConstructs(chunk, world);

       lock.unlock();
    }


    private void shelveConstructs(Chunk chunk, World world) {
        //discover on unload
        for (Entity entity: chunk.getEntities()) {

            //will require a translator
            net.minecraft.world.entity.Entity nms = ((CraftEntity)entity).getHandle();
            if (!(nms instanceof ComponentAS component))
                continue;

            if (entity.getPersistentDataContainer().has(key,PersistentDataType.INTEGER_ARRAY))
                continue;

            Construct struct = component.getBody();

            if (!struct.chunkLoaded())
                continue;

            struct.calculateOccupiedChunks();
            Set<Chunk> chunks = struct.getOccupiedChunks();

            Entity pivot = struct.getCoreEntity();
            struct.unload();

            activePieces.remove(struct);
            if (chunks.stream().filter(Chunk::isLoaded).count() - 1 <= 0)
                continue;

            //-1 because this chunk will be unloaded.
            ChunkTicket ticket = createTicket(chunks, struct, pivot, -1);
            addLoadingTicket(ticket,world);
        }
    }




    public void tick() {

        lock.lock();

        Iterator<ChunkTicket> iter = assembledTickets.iterator();
        while (iter.hasNext()) {

            ChunkTicket next = iter.next();
            if (next.isAssembled()) {
                next.finish();
                iter.remove();
                continue;
            }

            if (!next.isLoaded()) {
                iter.remove();
            }
        }
        lock.unlock();
    }




    //--------------------------------------------------



    public ChunkTicket createTicket(Set<Chunk> chunks, Construct construct, Entity pdc, int offset) {
        Set<Tuple2<Integer, Integer>> coords = new HashSet<>();
        int loaded = 0;
        for (Chunk chunk: chunks) {
            coords.add(new Tuple2<>(chunk.getX(), chunk.getZ()));
            if (chunk.isLoaded()) {
                loaded ++;
            }
        }
        loaded += offset;

        return new ChunkTicket(coords,loaded,construct, pdc);
    }

    public synchronized @Nullable Set<ChunkTicket> managerUpdate(String worldName, int x, int z, boolean onload) {
        WorldTicketManager manager = pieces.getOrDefault(worldName, null);
        if (manager == null)
            return null;

        return manager.update(x,z, onload);
    }


    //ticket for partially loaded constructs
    public void addLoadingTicket(ChunkTicket ticket, World world) {
        WorldTicketManager manager = pieces.get(world.getName());
        manager.addTicket(ticket);
    }



    public void updateTrackedWorlds(String worldName) {
        if (!pieces.containsKey(worldName)) {
            pieces.put(worldName, new WorldTicketManager(worldName));
        }
    }

    public void stop() {
        task.cancel();
    }


    public static Set<Construct> getActivePieces() {
        return activePieces;
    }

    public static void addActivePiece(Construct struct) {
        activePieces.add(struct);
    }

    public static void removeActivePiece(Construct struct) {
        activePieces.remove(struct);
    }
}
