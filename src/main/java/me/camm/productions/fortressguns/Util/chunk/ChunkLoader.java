package me.camm.productions.fortressguns.Util.chunk;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.Util.Math.IntTuple2;
import me.camm.productions.fortressguns.Util.Serialization.FactorySerialization;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.FortressGuns;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.camm.productions.fortressguns.Util.chunk.ChunkUtils.createTicket;
import static me.camm.productions.fortressguns.Util.chunk.ChunkUtils.isChunkEntityTicking;


public class ChunkLoader implements Listener {


    private final static Map<String, WorldTicketManager> pieces;


    private final static Set<Construct> activePieces;
    private final static Set<ChunkTicket> assembledTickets;
    private static ChunkLoader loader = null;
    private final NamespacedKey key;


    private final ReentrantLock lock;
    private final BukkitTask task;
    private final Logger logger;

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
        logger = FortressGuns.getInstance().getLogger();
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


        if (pieces.get(name).containsChunk(x,z))
            logger.log(Level.INFO, "Chunk loaded: "+x +" "+z);



        Set<ChunkTicket> tickets = managerUpdate(world.getName(),x,z,true);

        if (tickets != null) {
            for (ChunkTicket ticket: tickets) {
                logger.log(Level.INFO, "Ticket "+ticket.getUUID() +" awaiting assembly");
            }

            assembledTickets.addAll(tickets);
        }
        discoverConstructs(chunk);

        lock.unlock();
    }


    private void discoverConstructs(Chunk chunk) {

        World world = chunk.getWorld();
        for (Entity e: chunk.getEntities()) {
            PersistentDataContainer pdc = e.getPersistentDataContainer();
            if (!pdc.has(key, PersistentDataType.INTEGER_ARRAY))
                continue;

            Construct struct = FactorySerialization.deserializeConstruct(e.getLocation(),pdc, key);
            if (struct == null)
                continue;

            struct.calculateOccupiedChunks();
            Set<IntTuple2> loadedChunks = struct.getOccupiedChunks();

            int loaded = (int)loadedChunks.stream().filter(tup -> isChunkEntityTicking(world, tup.getA(), tup.getB())).count();

            ChunkTicket ticket;
            if (loaded >= loadedChunks.size()) {
                ticket = createTicket(loadedChunks, struct, e, 0); //1
                logger.log(Level.INFO, "Adding pre-assembled ticket "+ticket.getUUID());
                ticket.markLoadTime();
                assembledTickets.add(ticket);
            }
            else {
                ticket = createTicket(loadedChunks, struct, e, 0); //loaded
                logger.log(Level.INFO, "Adding loading ticket "+ticket.getUUID() +" at "+ticket.getCurrentLoaded() +" / " + ticket.getTotalChunks());
                logger.log(Level.INFO, ticket.toString());
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



            //world.getChunkAt(x,z) WILL load the chunk in question
            //this is probably causing some of the issues with your system
            //we removed them. now
            // you need to figure out what's causing the chunk loading callbacks to not fire

            struct.calculateOccupiedChunks();

            Set<IntTuple2> chunks = struct.getOccupiedChunks();

            Entity pivot = struct.getCoreEntity();
            struct.unload();

            activePieces.remove(struct);
            int loaded = (int)chunks.stream().filter(tup -> isChunkEntityTicking(world,tup.getA(), tup.getB())).count() -1;
            if (loaded <= 0)
                continue;

            //-1 because this chunk will be unloaded.
            ChunkTicket ticket = createTicket(chunks, struct, pivot, -1);
            addLoadingTicket(ticket,world);
        }
    }




    public void tick() {

        lock.lock();

        Iterator<ChunkTicket> iter = assembledTickets.iterator();
        Set<ChunkTicket> removals = new HashSet<>();

        while (iter.hasNext()) {
            ChunkTicket next = iter.next();
            if (next.isAssembled() && next.canFinish()) {
                logger.log(Level.INFO, "Completed ticket "+next.getUUID());
                next.finish();
                activePieces.add(next.getConstruct());
                removals.add(next);
                continue;
            }

            if (!next.isLoaded()) {
                removals.add(next);
            }
        }

        for (ChunkTicket ticket: removals) {
            managerRemove(ticket.getWorldName(), ticket);
            assembledTickets.remove(ticket);
        }

        lock.unlock();
    }




    //--------------------------------------------------



    public synchronized @Nullable Set<ChunkTicket> managerUpdate(String worldName, int x, int z, boolean onload) {
        WorldTicketManager manager = pieces.getOrDefault(worldName, null);
        if (manager == null)
            return null;

        return manager.update(x,z, onload);
    }


    private synchronized void managerRemove(String worldName, ChunkTicket ticket) {
        WorldTicketManager man = pieces.getOrDefault(worldName, null);
        if (man == null)
            return;
        man.removeTicket(ticket);


    }



    //ticket for partially loaded constructs
    public void addLoadingTicket(ChunkTicket ticket, World world) {
        WorldTicketManager manager = pieces.get(world.getName());
        manager.addTicket(ticket);
    }



    public void updateTrackedWorlds(String worldName) {
        if (!pieces.containsKey(worldName)) {
            pieces.put(worldName, new WorldTicketManager());
        }
    }



    public @Nullable List<UUID> getLoadingTickets(String worldName) {
        WorldTicketManager man = pieces.getOrDefault(worldName, null);
        if (man == null) return null;

        List<UUID> tickets = new ArrayList<>();
        man.getActiveTickets().forEach(ticket -> tickets.add(ticket.getUUID()));
        return tickets;

    }


    public String checkTicket(String worldName, String id) {
        WorldTicketManager man = pieces.getOrDefault(worldName, null);
        if (man == null) return "No manager present for world "+worldName;

        for (ChunkTicket ticket: man.getActiveTickets()) {
            if (ticket.getUUID().toString().equals(id)) {
             return ticket.toString();
            }
        }
        return "No ticket found";
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
