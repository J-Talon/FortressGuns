package me.camm.productions.fortressguns.interact.behaviour.ItemBehaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.Util.Math.Tuple3;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import me.camm.productions.fortressguns.interact.IBHandle;
import me.camm.productions.fortressguns.interact.InteractionBehaviourItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IBTacticalPointer implements InteractionBehaviourItem {


    private final Map<UUID, Tuple3<Integer, Integer, Long>> artSetting = new HashMap<>();
    static final int MAX = 100, MIN = 0;
//    private static final Logger logger = FortressGuns.getInstance().getLogger();;


    public Tuple3<Integer, Integer,Long> getTime(UUID id) {
        return artSetting.getOrDefault(id,new Tuple3<Integer, Integer,Long>((MAX - MIN) / 2,0,System.currentTimeMillis()));
    }


    @Override
    public boolean accept(Tuple2<Player, ItemStack> tup) {
       ItemStack main = tup.getB();
       Player context = tup.getA();

       if (FGItems.TACTICAL_PTR.isSimilar(main))
           return true;

       ItemStack offhand = context.getInventory().getItemInOffHand();
       return FGItems.TACTICAL_PTR.isSimilar(offhand);
    }

    @Override
    public @Nullable IBHandle getHandle() {
        return IBHandle.TPOINTER_SETTING;
    }

    @Override
    public Material[] getLabels() {
        return new Material[]{FGItems.TACTICAL_PTR.get().getType()};
    }

    @Override
    public void onScroll(PlayerItemHeldEvent event) {
        int to = event.getNewSlot();
        int from = event.getPreviousSlot();

        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInOffHand();


        if (!(FGItems.TACTICAL_PTR.isSimilar(stack))) {
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
        Rideable ride = ConstructUtils.getRideableRef(vehicle);

        if (ride == null) {
            time = updateSetting(time,dir,id);
            notifySettingChange(time, player);
            return;
        }

        time = updateSetting(time, dir, id);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.BLOCKS,1,(((float)time / MAX) * 2f));

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


    public int getSettingMax() {
        return MAX;
    }

    public int getSettingMin() {
        return MIN;
    }

    @Override
    public void onPrepareCraft(PrepareItemCraftEvent event) {
//        logger.log(Level.INFO, "onPrepareCraft");
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) { // if there is no result, no need to do stuff
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (RecipeManager.recipeUsesItemStrictly(recipe, item)) {
                continue;
            }

            if (FGItems.TACTICAL_PTR.isSimilar(item)) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    @Override
    public void onCraft(CraftItemEvent event) {
//        logger.log(Level.INFO, "CraftItemEvent");
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (RecipeManager.recipeUsesItemStrictly(recipe, item)) {
                continue;
            }

            if (FGItems.TACTICAL_PTR.isSimilar(item)) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
