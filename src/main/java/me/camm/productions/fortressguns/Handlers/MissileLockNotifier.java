package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Projectiles.SimpleMissile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MissileLockNotifier implements Runnable {

    private final ConcurrentHashMap<UUID, Integer> players;
    private final ConcurrentHashMap<UUID, Integer> time;

    private static MissileLockNotifier notifier = null;
    private static final Object lock = new Object();
    private static Thread thread;
    private final int MAX_SECONDS;
    private boolean running;
    private final Plugin plugin;

    private MissileLockNotifier(Plugin p){
        players = new ConcurrentHashMap<>();
        time = new ConcurrentHashMap<>();
        running = true;
        this.plugin = p;
        MAX_SECONDS = SimpleMissile.getFuelTicks() / 20;
    }

    public static MissileLockNotifier get(Plugin p) {
        if (notifier == null) {
            notifier = new MissileLockNotifier(p);
            thread = new Thread(notifier);
            thread.start();
        }
        return notifier;
    }

    public void resume() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void stop(){
        running = false;
        resume();
    }

    public void addNotification(UUID id) {

        Player target = Bukkit.getPlayer(id);
        if (target == null)
            return;

        int missiles = players.getOrDefault(id, 0);

        if (missiles == 0 && target.isGliding()) {
            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT,2f,1.2f);
            target.sendMessage(ChatColor.RED+"[WARNING] Projectiles Incoming!");
        }

        missiles ++;

        players.put(id, missiles);
        time.put(id, 0);

        resume();
    }

    public void exitNotification(UUID id) {
       int missiles = players.getOrDefault(id, -1);
       if (missiles == -1)
           return;

       if ((--missiles) <= 0) {
           time.remove(id);
           players.remove(id);
       }
       else players.replace(id, missiles);

    }


    public void removeNotification(UUID id) {
        players.remove(id);
        time.remove(id);
    }


    @Override
    public void run() {
        while (running) {
            try {

                synchronized (lock) {
                    if (players.isEmpty()) {
                        lock.wait();
                    }
                }

                    Thread.sleep(1000);

                    players.forEach((uuid, integer) -> {
                        Player player = Bukkit.getPlayer(uuid);

                        if (player == null || !player.isOnline()) {
                            players.remove(uuid);
                        }
                        else
                        {
                            int timeLeft = time.getOrDefault(uuid, -1);
                            if (timeLeft == -1) {
                                players.remove(uuid);
                            }
                            else
                            {
                                if (timeLeft > MAX_SECONDS) {
                                    players.remove(uuid);
                                    time.remove(uuid);
                                }
                                else {
                                    timeLeft ++;
                                    time.replace(uuid, timeLeft);
                                }
                            }

                            if (player.isGliding()) {
                                String out;
                                if (integer == 1) {
                                    out = "Missile Inbound";
                                } else out = "Missiles Inbound";


                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "[WARNING] " + integer + " " + out + " [WARNING]"));
                            }
                        }

                    });

            }
            catch (InterruptedException e) {
                plugin.getLogger().warning("Notifier for Missile lock has been interrupted: Players might not get notified:"+e.getMessage());
            }
        }
    }

}
