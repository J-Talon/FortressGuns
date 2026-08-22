package me.camm.productions.fortressguns.interact.behaviour.ConstructBehaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.interact.InteractionBehaviourCons;
import me.camm.productions.fortressguns.interact.item.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CBGlobalRiding implements InteractionBehaviourCons {


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
    public boolean accept(Tuple2<Player, ItemStack> item) {
        return item.getA().getVehicle() != null;
    }


    //if they rc the thing while riding there are 2 actions that can happen:
    //1: they fire the gun
    //2: they open an inventory
    @Override
    public void onRCCons(Construct struct, Component component, ItemStack mainHand, PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (ConstructUtils.isOperatorOf(player, struct)) event.setCancelled(true);

        if (struct instanceof RapidFire rapid && rapid.isJammed()) {
            openMenu(struct, player, mainHand);
            return;
        }

        if (ItemUtils.isAmmoItem(mainHand) != null) {
            openMenu(struct, player, mainHand);
            return;
        }

        if (struct instanceof Artillery arty && arty.canFire()) {
            arty.fire(player);
        }
    }




    //if they punch the vehicle and they're the rider, they shouldn't damage it
    @Override
    public void onLCCons(Construct struct, Component component, Player player, ItemStack mainHand, EntityDamageByEntityEvent event) {
        if (ConstructUtils.isOperatorOf(player, struct)) event.setCancelled(true);

        if (!(struct instanceof Artillery arty)) return;
        if (arty.canFire()) arty.fire(player);
    }
}
