package me.camm.productions.fortressguns.interact.behaviour.ConstructBehaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.interact.InteractionBehaviourCons;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.interact.item.ItemUtils;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;



//handles the opening of inventories and mounting the artillery
public class CBGlobalInteractStanding implements InteractionBehaviourCons {


    private final Logger logger;

    public CBGlobalInteractStanding(){
        logger = FortressGuns.getInstance().getLogger();

    }


    @Override
    public boolean treatGlobally() {
        return true;
    }

    @Override
    public @NotNull ConstructType[] getPrimaryLabels() {
        return new ConstructType[0];
    }

    @Override
    public @NotNull Material[] getSecondaryLabels() {
        return new Material[0];
    }


    @Override
    public void onRCCons(Construct struct, Component component, ItemStack mainHand, PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();
        event.setCancelled(true);

        if (!(struct instanceof Rideable ride)) {
            if (!player.isSneaking()) return;
            openMenu(struct, player, mainHand);
            return;
        }


        if (ride.getRider() != null) {
            player.sendMessage("Someone else is using this!");
            return;
        }

        if (ride.isSeatLocked()) {
            player.sendMessage("Seat is locked!");
            return;
        }

        ride.getSeat().startRiding(player);
        player.sendMessage("Operating "+ ChatColor.RESET+struct.getType().getName());
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.BLOCKS,1,1);


        new BukkitRunnable() {
            public void run() {
                if (struct.isInvalid() || !struct.chunkLoaded()) {
                    cancel();
                }

                if (!struct.equals(ConstructUtils.getRideableRef(player.getVehicle()))) {
                    ride.kickOperator();
                    cancel();
                }

                ride.rideTick(player);

            }
        }.runTaskTimer(FortressGuns.getInstance(),0,1);
    }


    @Override
    public boolean accept(Tuple2<Player, ItemStack> item) {
        Player player = item.getA();

        return (!FGItems.TACTICAL_PTR.isSimilar(item.getB())) &&
                                  player.getVehicle() == null;
    }
}
