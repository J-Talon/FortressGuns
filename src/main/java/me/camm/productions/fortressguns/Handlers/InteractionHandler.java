package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryCore;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.FortressGuns;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * @author CAMM
 */
public class InteractionHandler implements Listener
{
    private final Map<String, Class<? extends Artillery>> artilleryNames;
    private final ChunkLoader handler;

    public InteractionHandler(){
        handler = new ChunkLoader();
        artilleryNames = new HashMap<>();
        Plugin plugin = FortressGuns.getInstance();
        plugin.getServer().getPluginManager().registerEvents(handler, plugin);

        for (ArtilleryType type: ArtilleryType.values()) {
            artilleryNames.put(type.getName(), type.getClazz());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        handleArtilleryPlaceInteract(event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        ItemStack item = event.getItemInHand();

        if (item.getItemMeta() == null)
            return;

        if (item.getType()!=Material.CHEST)
            return;

        String name = item.getItemMeta().getDisplayName();
        if (artilleryNames.containsKey(name))
            event.setCancelled(true);


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        org.bukkit.entity.Entity riding = player.getVehicle();

        if (riding == null)
            return;


        //whenever the player quits, the server creates a new entity whenever they join back. We do not want this to happen,
        //so we dismount them first.
       Entity nms = ((CraftEntity)riding).getHandle();
       EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
       if (nms instanceof ArtilleryPart) {
           nmsPlayer.stopRiding();
       }
    }



    public void handleArtilleryPlaceInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();
        EntityPlayer nms = ((CraftPlayer)player).getHandle();

        if (!event.hasItem()) {


            Entity ride = nms.getVehicle();
            if (ride instanceof ArtilleryCore) {

                ArtilleryCore core = (ArtilleryCore) ride;
                Artillery body = core.getBody();

                if (body.canFire())
                   body.fire(player);
                event.setCancelled(true);

            }
            else if (ride instanceof ArtilleryPart) {
                ArtilleryPart part = (ArtilleryPart)ride;
                Artillery body = part.getBody();

                if (!(body instanceof RapidFire)) {
                    return;
                }

                if (body.canFire()) {
                    body.fire(player);
                    event.setCancelled(true);
                }

            }

        }

        if (action != Action.RIGHT_CLICK_AIR)
            return;

        ItemStack item = event.getItem();
        if (item == null)
            return;

        if (item.getItemMeta() == null)
            return;

        if (item.getType()!=Material.CHEST)
            return;


        //we used to do switches for modifiers. Now we do this :D
        String name = item.getItemMeta().getDisplayName();

        if (!artilleryNames.containsKey(name))
            return;



        if (player.isFlying() || !player.getLocation().clone().subtract(0,0.1,0).getBlock().getType().isSolid()) {

            player.sendMessage(ChatColor.RED+"You must be on the ground to assemble artillery.");
            return;
        }
        EulerAngle aim = new EulerAngle(Math.toRadians(nms.getXRot()),Math.toRadians(nms.getHeadRotation()),0);

            Class<? extends Artillery> artClass = artilleryNames.get(name);
            try {


               Class<?> value = artClass.getSuperclass();
               boolean isDirectional = false;
              while (value != null) {

                  if (value.equals(RapidFire.class)) {
                      isDirectional = true;
                      break;
                  }

                  value = value.getSuperclass();
              }

              if (!isDirectional) {
                  aim.setY(Math.min(aim.getY(), 0));
              }


              Artillery artillery = artClass
                      .getConstructor(Location.class, World.class, ChunkLoader.class, EulerAngle.class)
                      .newInstance(player.getLocation().clone()
                      .add(0,-0.5,0),player.getWorld(),handler,aim);


               artillery.spawn();


              Set<Chunk> chunks = artillery.getLoaders();
              for (Chunk c: chunks) {
                  handler.add(c, artillery);
              }


            }
            catch (Exception e) {
                e.printStackTrace();
            }


            event.setCancelled(true);

    }
}
