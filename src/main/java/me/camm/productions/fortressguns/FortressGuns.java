package me.camm.productions.fortressguns;

import me.camm.productions.fortressguns.Artillery.*;
import me.camm.productions.fortressguns.Artillery.Projectiles.Modifier.ModifierType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class FortressGuns extends JavaPlugin implements Listener {

    private static FortressGuns plugin;
    private final List<Artillery> guns;
    private volatile boolean aiming;

    public FortressGuns(){
        guns = new ArrayList<>();
    }

    public static Plugin getInstance(){
      return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
      plugin = this;
      this.aiming = false;

      getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public synchronized void setAiming(boolean aiming){
        this.aiming = aiming;
    }

    @EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent event){

        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();

        if (stack.getType()!=Material.BLAZE_ROD)
            return;

        Entity clicked = event.getRightClicked();
        net.minecraft.world.entity.Entity nms = ((CraftEntity)clicked).getHandle();
        player.sendMessage("Set target to "+clicked.getType());


        if (nms == null) {
            System.out.println("nms is null");
            return;
        }

        Iterator<Artillery> artilleryIterator = guns.iterator();

        while (artilleryIterator.hasNext()) {
            Artillery artillery = artilleryIterator.next();
            if (artillery.inValid()) {
                artillery.remove(false, true);
                artilleryIterator.remove();
            }
            else if (artillery instanceof HeavyFlak)
                ((HeavyFlak) artillery).setTarget(nms);

        }
    }

    @EventHandler
    public void onStickClick(PlayerInteractEvent event) {

            if (!event.hasItem())
                return;

            ItemStack stack = event.getItem();
            Player player = event.getPlayer();

            if (stack == null)
                return;


            if (stack.getType() != Material.STICK && stack.getType() != Material.REDSTONE_TORCH)
                return;

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                   Artillery gun = new HeavyFlak(player.getLocation(),player.getWorld());

                gun.spawn();
                guns.add(gun);
            }
            else if (event.getAction() == Action.RIGHT_CLICK_AIR) {

                if (stack.getType() == Material.REDSTONE_TORCH) {

                    setAiming(!aiming);

                    player.sendMessage("Aiming: "+aiming);
                    if (!aiming)
                        return;


                    Iterator<Artillery> artilleryIterator = guns.iterator();

                    while (artilleryIterator.hasNext()) {
                        Artillery artillery = artilleryIterator.next();
                        if (artillery.inValid()) {
                            artillery.remove(false, true);
                            artilleryIterator.remove();
                        }
                        else if (artillery instanceof HeavyFlak) {

                            new BukkitRunnable(){
                                @Override
                                public void run() {

                                    if (aiming)
                                    ((HeavyFlak) artillery).aimAtTarget();
                                    else
                                        cancel();
                                }
                            }.runTaskTimer(this,0,2);


                        }
                    }


                    return;
                }


                Location loc = player.getLocation();
                double yawRads = Math.PI * loc.getYaw() / 180;
                double pitchRads = Math.PI * loc.getPitch() / 180;

                Iterator<Artillery> artilleryIterator = guns.iterator();

                while (artilleryIterator.hasNext()) {
                    Artillery artillery = artilleryIterator.next();
                    if (artillery.inValid()) {
                        artillery.remove(false, true);
                        artilleryIterator.remove();
                    }
                    else
                        artillery.pivot(pitchRads, yawRads);
                }
            } else {

                Iterator<Artillery> artilleryIterator = guns.iterator();

                while (artilleryIterator.hasNext()) {
                    Artillery artillery = artilleryIterator.next();
                    if (artillery.inValid()) {
                        artillery.remove(false, true);
                        artilleryIterator.remove();
                    }
                    else
                        artillery.fire();
                }

            }


    }
}
