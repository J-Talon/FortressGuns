package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Util.Math.Tuple2;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class MissileLockNotifier implements Runnable {

    private final Map<UUID, Tuple2<Integer, Long>> entries;  //uuid, num missiles, expected end time stamp

    private static MissileLockNotifier notifier = null;

    private final ReentrantLock conditionLock;
    private final Condition condition;
    private final ReentrantLock dataLock;

    private final AtomicBoolean isSleeping;
    private final AtomicBoolean running;

    private final Logger logger;
    private boolean showRed;


    private MissileLockNotifier(Plugin p) {

        entries = new HashMap<>();

        conditionLock = new ReentrantLock();
        condition = conditionLock.newCondition();
        dataLock = new ReentrantLock(true);

        isSleeping = new AtomicBoolean(false);
        running = new AtomicBoolean(true);

        this.logger = p.getLogger();
    }

    public static MissileLockNotifier get(Plugin p) {
        if (notifier == null) {
            notifier = new MissileLockNotifier(p);
            Thread thread = new Thread(notifier);
            thread.start();
        }
        return notifier;
    }



    public synchronized void resume() {
        if (!isSleeping.get()) return;
        conditionLock.lock();
        isSleeping.set(false);
        condition.signal();
        conditionLock.unlock();
    }

    public void stop() {
        running.set(false);
        if (isSleeping.get())
            this.resume();
    }



    public void addNotification(UUID id, int fuelTicks) {

        if (!running.get()) return;


        dataLock.lock();
        Player target = Bukkit.getPlayer(id);
        if (target == null) {
            dataLock.unlock();
            return;
        }

        long now = System.currentTimeMillis();
        long endTS = (int)(fuelTicks * 1000.05) + now;
        Tuple2<Integer, Long> tup = entries.getOrDefault(id, null);
        if (tup == null) {
            if (target.isGliding()) {
                target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 2f, 1.2f);
                target.sendMessage(ChatColor.RED+"[WARNING] Projectiles Incoming!");
            }

            tup = new Tuple2<>(1, endTS);
            entries.put(id, tup);
            dataLock.unlock();

            resume();
            return;
        }

        tup.setA(tup.getA() + 1);
        if (endTS > tup.getB()) {
            tup.setB(endTS);
        }

        dataLock.unlock();
        resume();
    }

    public void exitNotification(UUID id) {

        dataLock.lock();
        Tuple2<Integer, Long> data = entries.getOrDefault(id, null);
        if (data == null) { dataLock.unlock(); return; }

        int missiles = data.getA();

       if ((--missiles) <= 0) {
           entries.remove(id);
       }
       else data.setA(missiles);
       dataLock.unlock();
    }


    public void removeNotification(UUID id) {
        dataLock.lock();
        entries.remove(id);
        dataLock.unlock();
    }


    @Override
    public void run() {
        try {
            List<UUID> removals = new ArrayList<>();

            while (running.get()) {

                dataLock.lock();
                if (entries.isEmpty()) {
                    dataLock.unlock();

                    conditionLock.lock();
                    isSleeping.set(true);


                    condition.await();
                    conditionLock.unlock();
                    continue;
                }


                entries.forEach((id, tup) -> {
                    Player player = Bukkit.getPlayer(id);
                    process: {
                        if (player == null || !player.isOnline()) {
                            removals.add(id);
                            break process;
                        }

                        long now = System.currentTimeMillis();
                        if (now > tup.getB()) {
                            removals.add(id);
                            break process;
                        }

                        if (!player.isGliding())
                            break process;

                        String out = (tup.getA()) > 1 ? "Missiles Inbound" : "Missile Inbound";
                        ChatColor color = showRed ? ChatColor.RED : ChatColor.WHITE;
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color + "[WARNING] " + tup.getA() + " " + out + " [WARNING]"));
                        }
                    }
                );

                for (UUID id: removals) entries.remove(id);

                dataLock.unlock();

                conditionLock.lock();
                condition.await(1, TimeUnit.SECONDS);
                conditionLock.unlock();

                showRed = !showRed;
            }
        }
        catch (InterruptedException e) {
            logger.warning("Missile notifier thread was interrupted: "+e.getMessage());
            running.set(false);
        }
    }
}
