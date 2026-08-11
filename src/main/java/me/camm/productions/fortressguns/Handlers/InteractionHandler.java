package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.ArtilleryRideable;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructFactory;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.item.ArtilleryItems.ItemUtils;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Util.Math.Tuple3;
import me.camm.productions.fortressguns.Util.chunk.ChunkLoader;
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
    private final static Map<UUID, Tuple3<Integer, Integer, Long>> artSetting = new HashMap<>();

    private final ChunkLoader handler;

    static final int MAX = 100, MIN = 0;





    public InteractionHandler(){
        handler = ChunkLoader.getInstance();
        Plugin plugin = FortressGuns.getInstance();
        plugin.getServer().getPluginManager().registerEvents(handler, plugin);
    }




    public static int getSettingMax() {
        return MAX;
    }

    public static int getSettingMin() {
        return MIN;
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
        ItemStack pointer = ItemUtils.getStick();

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

        if (!(nms instanceof ComponentAS)) {
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
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        org.bukkit.entity.Entity riding = player.getVehicle();

        if (riding == null)
            return;


        //whenever the player quits, the server creates a new entity whenever they join back. We do not want this to happen,
        //so we dismount them first.
       Entity nms = ((CraftEntity)riding).getHandle();
       EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
       if (nms instanceof ComponentAS) {
           nmsPlayer.stopRiding();

           Construct cons = ((ComponentAS) nms).getBody();

           if (cons instanceof Rideable) {
               ((Rideable) cons).onDismount();
           }
       }
    }





    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

    }




    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {

        org.bukkit.entity.Entity mount = event.getDismounted();
        Entity nms  = ((CraftEntity)mount).getHandle();

        if (!(nms instanceof ComponentAS)) {
            return;
        }

        Construct cons = ((ComponentAS) nms).getBody();

        if (cons instanceof Rideable) {
            ((Rideable) cons).onDismount();
        }

    }

}
