package me.camm.productions.fortressguns.interact.behaviour.ConstructBehaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.interact.InteractionBehaviourCons;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CBGlobalPointer implements InteractionBehaviourCons {


    @Override
    public @NotNull ConstructType[] getPrimaryLabels() {
        return ConstructType.values();
    }

    @Override
    public @NotNull Material[] getSecondaryLabels() {
        return new Material[]{FGItems.TACTICAL_PTR.get().getType()};
    }

    @Override
    public boolean accept(Tuple2<Player, ItemStack> item) {
        return item.getB().isSimilar(FGItems.TACTICAL_PTR.get());
    }



    //eventually we will have operation functions
    //such as making batteries and synchronization
    //but for now the only thing it really does is target, and that's tied to debugging so...
    //I'm just gonna leave this blank for now...
//    @Override
//    public void onRCCons(Construct struct, Component component, ItemStack mainHand, PlayerInteractEntityEvent event) {
//
//
//    }



    @Override
    public void onLCCons(Construct struct, Component component, Player player, ItemStack mainHand, EntityDamageByEntityEvent event) {
        if (!(struct instanceof Artillery arty)) return;
        if (arty.canFire()) arty.fire(player);
        event.setCancelled(true);
    }
}
