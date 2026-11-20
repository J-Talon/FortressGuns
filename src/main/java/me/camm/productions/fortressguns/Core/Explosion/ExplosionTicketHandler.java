package me.camm.productions.fortressguns.Core.Explosion;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class ExplosionTicketHandler implements Listener {

    private final Set<UUID> tickets;
    static ExplosionTicketHandler handler = null;
    private static final int THRESH = 60;

    private ExplosionTicketHandler() {
        tickets = new HashSet<>();
    }


    public static ExplosionTicketHandler getInstance() {
        if (handler == null)
            handler = new ExplosionTicketHandler();
        return handler;
    }

    public void addTicket(UUID id) {
        tickets.add(id);
    }

    public void removeTicket(UUID id) {
        tickets.remove(id);
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
       Item item1 = event.getEntity();

       if (tickets.contains(item1.getUniqueId()) && item1.getTicksLived() < THRESH) {
           event.setCancelled(true);
           return;
       }

       Item item2 = event.getTarget();
       if (tickets.contains(item2.getUniqueId()) && item2.getTicksLived() < THRESH) {
           event.setCancelled(true);
           return;
       }

       removeTicket(item1.getUniqueId());
       removeTicket(item2.getUniqueId());
    }
}
