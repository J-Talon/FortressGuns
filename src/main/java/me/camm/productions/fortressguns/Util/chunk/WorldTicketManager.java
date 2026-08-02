package me.camm.productions.fortressguns.Util.chunk;

import me.camm.productions.fortressguns.Util.Math.IntTuple2;
import me.camm.productions.fortressguns.Util.Math.Tuple2;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class WorldTicketManager {

    private final Map<Integer, Map<Integer, Map<UUID, ChunkTicket>>> tickets;
    private final ReentrantLock lock;

    public WorldTicketManager(String name) {
        tickets = new HashMap<>();
        this.lock = new ReentrantLock();
    }


    public void addTicket(ChunkTicket ticket) {

        lock.lock();
        Set<IntTuple2> chunks = ticket.getChunks();
        for (IntTuple2 current : chunks) {

            int x = current.getA();
            int z = current.getB();
            if (tickets.containsKey(x)) {
                Map<Integer, Map<UUID,ChunkTicket>> innerMap = tickets.get(x);


                if (innerMap.containsKey(z)) {
                    Map<UUID,ChunkTicket> set = innerMap.get(z);
                    set.putIfAbsent(ticket.getUUID(), ticket);
                } else {
                    Map<UUID,ChunkTicket> innerSet = new HashMap<>();
                    innerSet.put(ticket.getUUID(),ticket);
                    innerMap.put(z, innerSet);
                }
            } else {
                Map<Integer, Map<UUID,ChunkTicket>> innerMap = new HashMap<>();
                Map<UUID,ChunkTicket> innerSet = new HashMap<>();
                innerSet.put(ticket.getUUID(),ticket);
                innerMap.put(z, innerSet);
                tickets.put(x, innerMap);
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


            //outer map
            if (!tickets.containsKey(x)) {
                continue;
            }

            Map<Integer, Map<UUID,ChunkTicket>> innerMap = tickets.get(x);
            if (!innerMap.containsKey(z)) {
                continue;
            }

            Map<UUID,ChunkTicket> ticketsForChunk = innerMap.get(z);
            ticketsForChunk.remove(ticket.getUUID());

            if (ticketsForChunk.isEmpty()) {
                innerMap.remove(z);
            }

            if (innerMap.isEmpty()) {
                tickets.remove(x);
            }
        }

        lock.unlock();

    }


    //returns constructs which are fully loaded
    //after updating them
    public synchronized Set<ChunkTicket> update(int x, int z, boolean onLoad) {

        lock.lock();

        Map<Integer, Map<UUID,ChunkTicket>> innerMap = tickets.getOrDefault(x, null);
        if (innerMap == null) {
            lock.unlock();
            return null;
        }

        Map<UUID,ChunkTicket> ticketSet = innerMap.getOrDefault(z, null);
        if (ticketSet == null) {
            lock.unlock();
            return null;
        }

        Set<ChunkTicket> fullyLoaded = new HashSet<>();
        Set<ChunkTicket> fullyUnloaded = new HashSet<>();

        for (ChunkTicket ticket : ticketSet.values()) {
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
        for (Map<Integer, Map<UUID,ChunkTicket>> inner : tickets.values()) {
            for (Map<UUID,ChunkTicket> set : inner.values()) {
                activeTickets.addAll(set.values());
            }
        }
        return activeTickets;
    }


    public synchronized boolean containsChunk(int x, int z) {
        Map<Integer, Map<UUID,ChunkTicket>> i = tickets.getOrDefault(x, null);
        if (i == null)
            return false;

        return i.getOrDefault(z, null) != null;
    }
}
