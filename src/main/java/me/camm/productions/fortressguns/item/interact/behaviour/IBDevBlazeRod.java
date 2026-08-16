package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.MissileLauncher;
import me.camm.productions.fortressguns.Handlers.InteractionHandler;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.item.interact.IBHandle;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class IBDevBlazeRod implements InteractionBehaviourItem {

    private MissileLauncher launcher;

    @Override
    public boolean accept(Tuple2<Player, ItemStack> item) {
        return Material.BLAZE_ROD == item.getB().getType();
    }

    @Override
    public void onRCAir(PlayerInteractEvent event) {
        InteractionHandler handler = InteractionHandler.getInstance();
        IBDevSpyglass s = (IBDevSpyglass) handler.getItemBehaviour(IBHandle.DEV_SPYGLASS_TARGET);
        if (s == null) return;
        Entity target = s.getTarget(event.getPlayer().getUniqueId());

        if (launcher == null)
            return;

        launcher.setTarget(target);
        launcher.fire(event.getPlayer());
    }


    @Override
    public @Nullable IBHandle getHandle() {
        return IBHandle.DEV_BR;
    }

    @Override
    public Material[] getLabels() {
        return new Material[]{Material.BLAZE_ROD};
    }

    public void setLauncher(MissileLauncher l) {
        this.launcher = l;
    }
}
