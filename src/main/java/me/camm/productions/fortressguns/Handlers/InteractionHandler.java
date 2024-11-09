package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.MissileLauncher;
import me.camm.productions.fortressguns.FortressGuns;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
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
import org.bukkit.util.RayTraceResult;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;


/*
 * @author CAMM
 */
public class InteractionHandler implements Listener
{
    private final Map<String, Class<? extends Artillery>> artilleryNames;
    private final static Map<UUID, org.bukkit.entity.Entity> targets = new HashMap<>();

    private final ChunkLoader handler;


    private MissileLauncher debug = null;


    public InteractionHandler(){
        handler = new ChunkLoader();


        artilleryNames = new HashMap<>();
        Plugin plugin = FortressGuns.getInstance();
        plugin.getServer().getPluginManager().registerEvents(handler, plugin);

        for (ArtilleryType type: ArtilleryType.values()) {
            artilleryNames.put(type.getName(), type.getClazz());
        }
    }


    public static void updateTarget(UUID id, org.bukkit.entity.Entity target) {
        targets.put(id, target);
    }

    public static org.bukkit.entity.Entity getTarget(UUID id) {
        return targets.getOrDefault(id, null);
    }

    public void findTarget(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        if (stack == null || stack.getItemMeta() == null) {
            return;
        }

        if (stack.getType() != Material.SPYGLASS)
            return;

        if (event.getAction() != Action.LEFT_CLICK_AIR)
            return;

        Player player = event.getPlayer();
        World world = player.getWorld();

        Predicate<org.bukkit.entity.Entity> entityPredicate = new Predicate<org.bukkit.entity.Entity>() {
            @Override
            public boolean test(org.bukkit.entity.Entity entity) {
                return !(entity.equals(player));
            }
        };

        RayTraceResult res = world.rayTraceEntities(player.getEyeLocation(),player.getEyeLocation().getDirection(),200, 1, entityPredicate);
        if (res == null)
            return;

        org.bukkit.entity.Entity hit = res.getHitEntity();
        if (hit == null)
            return;

        updateTarget(player.getUniqueId(), hit);

        player.playSound(player.getLocation(),Sound.ENTITY_ARROW_HIT_PLAYER,1,1);
        player.sendMessage(ChatColor.RED+"Target Acquired: "+hit.getType());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        findTarget(event);
        handleArtilleryInteract(event);
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        ItemStack item = event.getItemInHand();

        if (item.getItemMeta() == null)
            return;

        /**************************/
//    //    debug
//        if (item.getType() == Material.BARRIER) {
//            ArmorStand s = event.getPlayer().getWorld().spawn(event.getPlayer().getLocation(), ArmorStand.class);
//            System.out.println(s.getLocation().toString());
//            System.out.println(s.getBoundingBox().toString());
//        }
//      //  debug
        /********************************/



        if (item.getType()!=Material.CHEST)
            return;



        String name = item.getItemMeta().getDisplayName();
        if (artilleryNames.containsKey(name)) {
            event.getPlayer().sendMessage(ChatColor.RED+"[!] Right click the air if you're trying to assemble artillery.");
            event.setCancelled(true);
        }
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
       if (nms instanceof Component) {
           nmsPlayer.stopRiding();

           if (nms instanceof ArtilleryPart) {
               ((ArtilleryPart)nms).getBody().setHasRider(false);
           }

       }
    }



    public void handleArtilleryInteract(PlayerInteractEvent event) {


        Player player = event.getPlayer();
        Action action = event.getAction();
        EntityPlayer nms = ((CraftPlayer)player).getHandle();

        if (!event.hasItem()) {
            Entity ride = nms.getVehicle();
            if (ride instanceof ArtilleryPart) {
                ArtilleryPart part = (ArtilleryPart)ride;
                Artillery body = part.getBody();

                if (!part.equals(body.getRotatingSeat())) {
                    return;
                }

                Action a = event.getAction();
                if (a != Action.LEFT_CLICK_AIR)
                    return;

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

//        /***************************************/
///////debug
//        if (item.getType() == Material.BLAZE_ROD) {
//            if (debug != null)
//                debug.fire(player);
//        }
///////////
//        /***************************************/

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
                 aim = aim.setX(Math.min(aim.getX(), 0));
              }

                World world = player.getWorld();
                Location loc = player.getLocation().clone().add(0,-0.6,0);
                // -0.6 so it's on the ground

              Artillery artillery = artClass
                      .getConstructor(Location.class, World.class, ChunkLoader.class, EulerAngle.class)
                      .newInstance(loc,world,handler,aim);

              world.playSound(loc,Sound.BLOCK_ANVIL_DESTROY,0.5f,1);
               boolean spawned = artillery.spawn();

               if (!spawned) {
                   player.sendMessage(ChatColor.RED + "There is not enough space here to build an artillery piece!");
                   return;
               }



              Set<Chunk> chunks = artillery.getOccupiedChunks();
              for (Chunk c: chunks) {
                  handler.add(c, artillery);
              }

//              //debug
//                /***************************************/
//              if (artillery instanceof MissileLauncher) {
//                  debug = (MissileLauncher) artillery;
//                  updateTarget(player.getUniqueId(), player);
//              }
//                /***************************************/


            }
            catch (Exception e) {
                e.printStackTrace();
            }


            event.setCancelled(true);

    }


    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {

        org.bukkit.entity.Entity mount = event.getDismounted();

        Entity nms  = ((CraftEntity)mount).getHandle();
        if (nms instanceof ArtilleryPart) {
            Artillery arty = ((ArtilleryPart)nms).getBody();
            arty.setHasRider(false);
        }
    }
}
