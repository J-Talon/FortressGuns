package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Util.Tuple2;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WorldTicketManager {

    private final Map<Integer, Map<Integer, Set<ChunkTicket>>> tickets;
    static int id = 0;
    String name;

    public WorldTicketManager(String name) {
        tickets = new HashMap<>();
        id ++;
        this.name = name;
    }




    public synchronized void addTicket(ChunkTicket ticket) {

        Set<Tuple2<Integer, Integer>> chunks = ticket.getChunks();
        for (Tuple2<Integer, Integer> current: chunks) {

            int x = current.getA();
            int z = current.getB();
            if (tickets.containsKey(x)) {
                Map<Integer, Set<ChunkTicket>> innerMap = tickets.get(x);


                if (tickets.containsKey(z)) {
                    Set<ChunkTicket> set = innerMap.get(z);
                    set.add(ticket);
                }
                else {
                Set<ChunkTicket> innerSet = new HashSet<>();
                innerSet.add(ticket);
                innerMap.put(z, innerSet);
                }
            }
            else {
                Map<Integer, Set<ChunkTicket>> innerMap = new HashMap<>();
                Set<ChunkTicket> innerSet = new HashSet<>();
                innerSet.add(ticket);
                innerMap.put(z, innerSet);
                tickets.put(x,innerMap);
            }
        }
    }


    //returns constructs which are fully loaded or unloaded after updating them
    public synchronized @Nullable Set<ChunkTicket> update(int x, int z, boolean onLoad) {

        Map<Integer, Set<ChunkTicket>> innerMap = tickets.getOrDefault(x, null);
        if (innerMap == null)
            return null;

        Set<ChunkTicket> ticketSet = innerMap.getOrDefault(z, null);
        if (ticketSet == null)
            return null;

        Set<ChunkTicket> thresholdChunks = new HashSet<>();
        Iterator<ChunkTicket> iter = ticketSet.iterator();
        System.out.println("Updating chunk: "+x+" "+z+" | onload: "+onLoad);
        while (iter.hasNext()) {
            ChunkTicket ticket = iter.next();

            if (onLoad) {
                if (ticket.onLoad()) {
                    thresholdChunks.add(ticket);
                    iter.remove();
                }
            }
            else {
                if (!ticket.onUnload()) {
                    thresholdChunks.add(ticket);
                    iter.remove();
                }
            }
        }

        if (ticketSet.isEmpty()) {
            innerMap.remove(z);
        }

        if (innerMap.isEmpty()) {
            tickets.remove(x);
        }

        return thresholdChunks;
    }


    public synchronized @Nullable Set<ChunkTicket> unloadReturnIntermediate(int x, int z) {
        Map<Integer, Set<ChunkTicket>> innerMap = tickets.getOrDefault(x, null);
        if (innerMap == null)
            return null;

        Set<ChunkTicket> ticketSet = innerMap.getOrDefault(z, null);
        if (ticketSet == null)
            return null;

        Set<ChunkTicket> thresholdChunks = new HashSet<>();

        Iterator<ChunkTicket> iter = ticketSet.iterator();

        System.out.println("Unload immed chunk: "+x+" "+z);
        while (iter.hasNext()) {
            ChunkTicket ticket = iter.next();

            //if some or all of the chunks are unloaded
            if (ticket.onUnload() || ticket.allUnloaded()) {
                thresholdChunks.add(ticket);
                iter.remove();
            }
        }

        if (ticketSet.isEmpty()) {
            innerMap.remove(z);
        }

        if (innerMap.isEmpty()) {
            tickets.remove(x);
        }

        return thresholdChunks;
    }




    public synchronized Set<ChunkTicket> getActiveTickets() {
        Set<ChunkTicket> activeTickets = new HashSet<>();
        for (Map<Integer, Set<ChunkTicket>> inner: tickets.values()) {
            for (Set<ChunkTicket> set: inner.values()) {
                activeTickets.addAll(set);
            }
        }
        return activeTickets;
    }
}
