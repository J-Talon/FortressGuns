package me.camm.productions.fortressguns.interact.behaviour.ItemBehaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.interact.InteractionBehaviourItem;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;



public class IBConstructs implements InteractionBehaviourItem {

    //any item
    @Override
    public boolean accept(Tuple2<Player, ItemStack> tup) {
        return true;
    }

    @Override
    public Material[] getLabels() {
        return new Material[]{Material.AIR};
    }

    @Override
    public void onLCAir(PlayerInteractEvent event) {
        /*
    Although this could very easily be put into the construct classes, I find it better that it's actually here, cause
    this way we can make it more version independent should we choose

    Also this prevents the construct logic from being too big of a file
 */
        Player player = event.getPlayer();
        Entity ride = player.getVehicle();

        if (ride == null)
            return;

        //this has the side effect of checking that it is indeed a cons
        Rideable rideable = ConstructUtils.getRideableRef(ride);

        if (rideable == null) return;

        this.interactArtillery((Construct) rideable, player);
        event.setCancelled(true);

    }

    private void interactArtillery(Construct cons, Player player) {
        if (!(cons instanceof Artillery arty)) {
            return;
        }

        if (arty.canFire()) {
            arty.fire(player);
        }

    }
}
