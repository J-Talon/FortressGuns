package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.MissileLauncher;
import me.camm.productions.fortressguns.Handlers.InteractionHandler;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.item.interact.IBHandle;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class IBDevBlazeRod implements InteractionBehaviourItem {

    private MissileLauncher launcher;

    @Override
    public boolean accept(Tuple2<Player, ItemStack> item) {
        return Material.BLAZE_ROD == item.getB().getType();
    }

    @Override
    public void onRCAir(PlayerInteractEvent event) {

        if (launcher == null)
            return;
        launcher.setTarget(event.getPlayer());
        launcher.fire(event.getPlayer());
    }


    private void notifNoTarget(Player player) {
        player.sendMessage("No entity found");
    }

    @Override
    public void onLCAir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        class PredicateSelf implements Predicate<Entity> {

            @Override
            public boolean test(Entity entity) {
                return !(entity.getUniqueId().equals(player.getUniqueId()));
            }
        }


        RayTraceResult res = world.rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 30, new PredicateSelf());

        if (res == null) {
            notifNoTarget(player);
            return;
        }

        Entity hit = res.getHitEntity();
        if (hit == null) {
            notifNoTarget(player);
            return;
        }

        player.sendMessage(hit.getType().name());

        net.minecraft.world.entity.Entity nms = ((CraftEntity)hit).getHandle();

        if (!(nms instanceof Component comp)) {
            notifNoTarget(player);
            return;
        }


        Construct cons = comp.getBody();
        if (cons.getType() == ConstructType.MISSILE_LAUNCHER) {
            setLauncher((MissileLauncher) cons);
            player.sendMessage("Set launcher");
            return;
        }

        notifNoTarget(player);

    }

    @Override
    public Material[] getLabels() {
        return new Material[]{Material.BLAZE_ROD};
    }

    public void setLauncher(MissileLauncher l) {
        this.launcher = l;
    }
}
