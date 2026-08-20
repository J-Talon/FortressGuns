package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.Util.Math.Tuple3;
import me.camm.productions.fortressguns.interact.InteractionBehaviourCons;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EntityInteractionHandler implements Listener {



    enum EntityBehaviour {



    }



    private static EntityInteractionHandler instance = null;
    private final Map<Material, List<InteractionBehaviourCons>> entityInteractions;
    private final Logger logger;


    private EntityInteractionHandler() {
        entityInteractions = new HashMap<>();
        logger = FortressGuns.getInstance().getLogger();
    }

    public static EntityInteractionHandler get() {
        if (instance == null) instance = new EntityInteractionHandler();
        return instance;
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




    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        org.bukkit.entity.Entity clicked = event.getRightClicked();
        Entity nms = ((CraftEntity)clicked).getHandle();

        ConstructType type = null;
        Construct body = null;
        if (!(nms instanceof Component comp)) return;
        body = comp.getBody();

        if (body == null) {
            logger.warning("onPlayerInteractEntity: Interacted component had no body!");
            return;
        }

        type = body.getType();

        ItemStack main = player.getInventory().getItemInMainHand();
        Material mat = main.getType();

        List<InteractionBehaviourCons> interactionBehaviour = entityInteractions.getOrDefault(mat, null);
        if (interactionBehaviour == null) return;

        Tuple3<ConstructType, ItemStack, Player> tup = new Tuple3<>(type, main, player);

        for (InteractionBehaviourCons interaction: interactionBehaviour) {
            if (!interaction.accept(tup)) continue;

            interaction.onRCCons(body, comp, main, event);
        }
    }


    @EventHandler
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {

        org.bukkit.entity.Entity damager = event.getDamager();
        org.bukkit.entity.Entity damagee = event.getEntity();

        if (damager.getType() != EntityType.PLAYER) return;

        ItemStack main = ((Player)damager).getInventory().getItemInMainHand();

        EntityDamageEvent.DamageCause cause = event.getCause();

        //shouldn't be sweeping either
        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;


        if (damagee.getType() == EntityType.ARMOR_STAND) {
            Tuple2<Construct, Component> res = checkASConstructs(event);
            if (res == null) return;
            process(res, (Player)damager, main, event);

            return;
        }

        //...

    }


    private @Nullable Tuple2<Construct, Component> checkASConstructs(EntityDamageByEntityEvent event) {

        org.bukkit.entity.Entity damagee = event.getEntity();
        Entity nms = ((CraftEntity)damagee).getHandle();

        if (!(nms instanceof Component comp)) return null;

        Construct struct = comp.getBody();
        return new Tuple2<>(struct, comp);
    }



    private void process(Tuple2<Construct, Component> tup, Player player, ItemStack main, EntityDamageByEntityEvent event) {
        if (tup == null) return;

        Material mainMat = main == null ? Material.AIR : main.getType();
        List<InteractionBehaviourCons> interactions = entityInteractions.getOrDefault(mainMat, null);
        if (interactions == null) return;

        Tuple3<ConstructType, ItemStack, Player> pack = new Tuple3<>(tup.getA().getType(),main, player);

        for (InteractionBehaviourCons cons: interactions) {
            if (cons.accept(pack)) {
                cons.onLCCons(tup.getA(),tup.getB(),player, main, event);
                return;
            }
        }
    }

}
