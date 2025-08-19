package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.ArtilleryRideable;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructFactory;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.FactorySerialization;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.ArtilleryItems.ConstructItemHelper;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Util.Tuple3;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import org.bukkit.util.RayTraceResult;
import org.spigotmc.event.entity.EntityDismountEvent;


import java.util.HashMap;
import java.util.Map;

import java.util.UUID;
import java.util.function.Predicate;


/*
 * @author CAMM
 */
public class InteractionHandler implements Listener
{
    private final static Map<UUID, org.bukkit.entity.Entity> targets = new HashMap<>();
    private final static Map<UUID, Tuple3<Integer, Integer, Long>> artSetting = new HashMap<>();

    private final ChunkLoader handler;

    static final int MAX = 100, MIN = 0;




    public InteractionHandler(){
        handler = new ChunkLoader();
        Plugin plugin = FortressGuns.getInstance();
        plugin.getServer().getPluginManager().registerEvents(handler, plugin);
    }


    public static void updateTarget(UUID id, org.bukkit.entity.Entity target) {
        targets.put(id, target);
    }

    public static int getSettingMax() {
        return MAX;
    }

    public static int getSettingMin() {
        return MIN;
    }

    public static org.bukkit.entity.Entity getTarget(UUID id) {
        return targets.getOrDefault(id, null);
    }


    public static Tuple3<Integer, Integer,Long> getTime(UUID id) {
        return artSetting.getOrDefault(id,new Tuple3<Integer, Integer,Long>((MAX - MIN) / 2,0,System.currentTimeMillis()));
    }



    @EventHandler
    public void onPlayerScroll(PlayerItemHeldEvent event) {

        int to = event.getNewSlot();
        int from = event.getPreviousSlot();

        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInOffHand();
        ItemStack pointer = ConstructItemHelper.getStick();

        if (!(pointer.isSimilar(stack))) {
            return;
        }

        int diff = to-from;
        int diffAbs = Math.abs(diff);


        UUID id = player.getUniqueId();
        Tuple3<Integer, Integer,Long> trip = getTime(id);
        int time = trip.getA();
        int dir = trip.getB();
        long lastAction = trip.getC();



        if (diffAbs == 8) {
            time -= (diff / diffAbs);
            dir = (diff / diffAbs);
        }
        else if (diffAbs == 1 || (System.currentTimeMillis() - lastAction > 700)) {
            if (diff < 0) {
                dir = -1;
                time -= diffAbs;
            } else {
                time += diffAbs;
                dir = 1;
            }
        }
        else {
            if (dir > 0) {
                time += diffAbs;
            }
             else {
                time -= diffAbs;
            }
        }


        org.bukkit.entity.Entity vehicle = player.getVehicle();
        if (vehicle == null) {
            time = updateSetting(time,dir,id);
            notifySettingChange(time, player);
            return;
        }

        Entity nms = ((CraftEntity)vehicle).getHandle();

        if (!(nms instanceof Component)) {
            time = updateSetting(time, dir, id);
            notifySettingChange(time, player);
            return;
        }

        time = updateSetting(time, dir, id);
        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_HAT,SoundCategory.BLOCKS,1,(((float)time / MAX) * 2f));



        /*
         1 - 2 --> increase
         0 - 9 --> increase

         9 - 0 --> decrease
         2 - 1 --> decrease
         */
    }

    private int updateSetting(int time, int dir, UUID id) {
        time = Math.max(MIN,time);
        time = Math.min(MAX,time);

        artSetting.put(id, new Tuple3<>(time, dir, System.currentTimeMillis()));
        return time;
    }

    private void notifySettingChange(int time, Player player) {
        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_HAT,SoundCategory.BLOCKS,1,(((float)time / MAX) * 2f));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.GOLD+"Power/Fuse: ["+time+"/"+MAX+"] (Ticks/Percent)"));
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        ItemStack item = event.getItemInHand();
        if (ConstructItemHelper.holdsConstruct(item) != null) {
            event.getPlayer().sendMessage(ChatColor.RED+"[!] Right click the air if you're trying to assemble artillery.");
            event.setCancelled(true);
            return;
        }

        if (ConstructItemHelper.isAmmoItem(item) != null) {
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

           Construct cons = ((Component) nms).getBody();

           if (cons instanceof Rideable) {
               ((Rideable) cons).onDismount();
           }
       }
    }





    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        ItemStack stack = event.getItem();
        if (ConstructItemHelper.isAmmoItem(stack) != null) {
            Player player = event.getPlayer();

            org.bukkit.entity.Entity ride = player.getVehicle();
            if (ride == null || !ride.isValid() || ride.isDead())
                return;

            Entity nms = ((CraftEntity)ride).getHandle();


            if (!(nms instanceof Component)) {
                return;
            }

            Construct cons = ((Component) nms).getBody();

            if (!(cons instanceof ArtilleryRideable rideable)) {
                return;
            }

            InventoryGroup group = rideable.getInventoryGroup();
            if (rideable instanceof RapidFire rapid && rapid.isJammed()) {
                group.openInventory(InventoryCategory.JAM_CLEAR, player);
            }
            else {
                group.openInventory(InventoryCategory.RELOADING, player);
            }
            return;
        }

        findTarget(event);
        handleArtilleryInteract(event);
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
        player.sendMessage(ChatColor.RED+"[Development only] Target Acquired: "+hit.getType());
    }




    public void handleArtilleryInteract(PlayerInteractEvent event) {


        Player player = event.getPlayer();
        Action action = event.getAction();
        EntityPlayer nms = ((CraftPlayer)player).getHandle();


        //logic for when they're operating a gun and are interacting
        Entity ride = nms.getVehicle();
        if (ride instanceof ArtilleryPart part) {
            Artillery body = part.getBody();

            if (!(body instanceof Rideable)) {
                //how the heck did you manage to ride the artillery???
                return;
            }

            if (!part.equals(((Rideable) body).getSeat())) {
                return;
            }

            Action a = event.getAction();
            if (a != Action.LEFT_CLICK_AIR)
                return;

            //this is for the guns which don't have a fire trigger
            //otherwise the logic in the firetrigger handles the shooting
            if (body.canFire()) {
                body.fire(player);
                event.setCancelled(true);
            }
        }

        if (action != Action.RIGHT_CLICK_AIR)
            return;


        ItemStack item = event.getItem();
        ConstructType type = ConstructItemHelper.holdsConstruct(item);

            if (type == null)
                return;

        if (player.isFlying() || !player.getLocation().clone().subtract(0,0.1,0).getBlock().getType().isSolid()) {
            player.sendMessage(ChatColor.RED+"[!] You must be on the ground to assemble artillery.");
            return;
        }

        int x = (int)(Math.toRadians(nms.getXRot()) * 100);
        int z = (int)(Math.toRadians(nms.getHeadRotation()) * 100);

        //temporary
        ConstructFactory<? extends Construct> factory = type.getFactory();
        Construct cons = factory.create(player.getLocation().add(0,-0.6,0), type.ordinal(),x,z, 0);

        if (cons != null) {
            boolean success = cons.spawn();
            System.out.println("construct chunk size: "+cons.getOccupiedChunks().size());
            if (!success) {
                player.sendMessage(ChatColor.RED+"[!] There is not enough space here to assemble this artillery.");
            }
            else {
                handler.addActiveTicket(handler.createTicket(cons.getOccupiedChunks(),cons,player.getWorld()),player.getWorld());
                player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_PLACE,1,1);
            }
        }
        else {
            player.sendMessage(ChatColor.RED+"[!] Unable to create artillery. This is probably a bug.");
        }





            event.setCancelled(true);

    }


    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {

        org.bukkit.entity.Entity mount = event.getDismounted();
        Entity nms  = ((CraftEntity)mount).getHandle();

        if (!(nms instanceof Component)) {
            return;
        }

        Construct cons = ((Component) nms).getBody();

        if (cons instanceof Rideable) {
            ((Rideable) cons).onDismount();
        }

    }

}
