package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Util.Tuple2;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WorldTicketManager {

    private final Map<Integer, Map<Integer, Map<UUID, ChunkTicket>>> tickets;
    static int id = 0;
    String name;

    public WorldTicketManager(String name) {
        tickets = new HashMap<>();
        id++;
        this.name = name;
    }


    public synchronized void addTicket(ChunkTicket ticket) {

        Set<Tuple2<Integer, Integer>> chunks = ticket.getChunks();
        for (Tuple2<Integer, Integer> current : chunks) {

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
    }

    public synchronized void removeTicket(ChunkTicket ticket) {
        Set<Tuple2<Integer, Integer>> chunks = ticket.getChunks();

        for (Tuple2<Integer, Integer> chunk : chunks) {
            int x = chunk.getA();
            int z = chunk.getB();

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

    }


    //returns constructs which are fully loaded or unloaded after updating them
    public synchronized Set<ChunkTicket> update(int x, int z, boolean onLoad) {

        Map<Integer, Map<UUID,ChunkTicket>> innerMap = tickets.getOrDefault(x, null);
        if (innerMap == null)
            return null;

        Map<UUID,ChunkTicket> ticketSet = innerMap.getOrDefault(z, null);
        if (ticketSet == null)
            return null;

        Set<ChunkTicket> thresholdChunks = new HashSet<>();

        for (ChunkTicket ticket : ticketSet.values()) {
            if (onLoad) {
                if (ticket.onLoad()) {
                    thresholdChunks.add(ticket);
                }
            } else {
                if (!ticket.onUnload()) {
                    thresholdChunks.add(ticket);
                }
            }
        }

        for (ChunkTicket ticket : thresholdChunks) {
            removeTicket(ticket);
        }

        return thresholdChunks;
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
