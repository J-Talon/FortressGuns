package me.camm.productions.fortressguns.Util.chunk;

import me.camm.productions.fortressguns.Util.Math.IntTuple2;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class WorldTicketManager {

    private final Map<Long, Map<UUID, ChunkTicket>> tickets;
    private final ReentrantLock lock;

    public WorldTicketManager() {
        tickets = new HashMap<>();
        this.lock = new ReentrantLock();
    }


    public void addTicket(ChunkTicket ticket) {

        lock.lock();
        Set<IntTuple2> chunks = ticket.getChunks();
        for (IntTuple2 current : chunks) {

            int x = current.getA();
            int z = current.getB();

            long id = ChunkUtils.chunkId(x,z);

            Map<UUID, ChunkTicket> inner;
            if (tickets.containsKey(id)) {
                inner = tickets.get(id);
                inner.putIfAbsent(ticket.getUUID(), ticket);
            }
            else {
              inner = new HashMap<>();
              inner.put(ticket.getUUID(), ticket);
              tickets.put(id, inner);

            }
        }

        lock.unlock();
    }

    public synchronized void removeTicket(ChunkTicket ticket) {

        lock.lock();
        Set<IntTuple2> chunks = ticket.getChunks();

        for (IntTuple2 chunk : chunks) {
            int x = chunk.getA();
            int z = chunk.getB();

            long id = ChunkUtils.chunkId(x,z);
            if (!tickets.containsKey(id)) {
                continue;
            }

            Map<UUID, ChunkTicket> inner = tickets.get(id);
            inner.remove(ticket.getUUID());

            if (inner.isEmpty()) {
                tickets.remove(id);
            }
        }

        lock.unlock();

    }


    //returns constructs which are fully loaded
    //after updating them
    public synchronized Set<ChunkTicket> update(int x, int z, boolean onLoad) {

        lock.lock();
        long id = ChunkUtils.chunkId(x,z);

        Map<UUID,ChunkTicket> innerMap = tickets.getOrDefault(id, null);
        if (innerMap == null) {
            lock.unlock();
            return null;
        }

        Set<ChunkTicket> fullyLoaded = new HashSet<>();
        Set<ChunkTicket> fullyUnloaded = new HashSet<>();

        for (ChunkTicket ticket : innerMap.values()) {
            if (onLoad) {
                if (ticket.onLoad())  //the ticket is fully loaded
                    fullyLoaded.add(ticket);

            } else {
                if (!ticket.onUnload())  //if the ticket is completely unloaded, remove it
                    fullyUnloaded.add(ticket);
            }
        }

        for (ChunkTicket ticket : fullyUnloaded) {
            removeTicket(ticket);
        }

        lock.unlock();
        return fullyLoaded;
    }


    public synchronized Set<ChunkTicket> getActiveTickets() {
        Set<ChunkTicket> activeTickets = new HashSet<>();
        for (Map<UUID,ChunkTicket> inner : tickets.values()) {
                activeTickets.addAll(inner.values());
        }
        return activeTickets;
    }

}
