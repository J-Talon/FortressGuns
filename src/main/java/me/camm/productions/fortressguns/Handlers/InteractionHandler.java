package me.camm.productions.fortressguns.Handlers;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Math.Tuple2;

import me.camm.productions.fortressguns.interact.IBHandle;
import me.camm.productions.fortressguns.interact.InteractionBehaviour;
import me.camm.productions.fortressguns.interact.InteractionBehaviourCons;
import me.camm.productions.fortressguns.interact.InteractionBehaviourItem;
import me.camm.productions.fortressguns.interact.behaviour.ItemBehaviour.*;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Consumer;
import java.util.logging.Logger;


enum ItemBehaviour {
    DEV_SPYGLASS(new IBDevSpyglass()),  //DEV_SPYGLASS_TARGET
    DEV_BLAZEROD(new IBDevBlazeRod()),

    AMMO_ITEM(new IBAmmoItem()),
    CREATE_CONSTRUCT(new IBConstructBox()),
    RIDING_CONSTRUCT(new IBConstructs()),
    TACTICAL_PT(new IBTacticalPointer()), //TPOINTER_SETTING
    FLARE_GUN(new IBFlareGun()),
    FLARE(new IBFlare());

    private final InteractionBehaviourItem behaviour;

    private ItemBehaviour(InteractionBehaviourItem behaviour) {
        this.behaviour =  behaviour;
    }

    public InteractionBehaviourItem getBehaviour() {
        return behaviour;
    }
}



enum ConstructBehaviour {
    ;

    private final InteractionBehaviourCons behaviour;

    private ConstructBehaviour(InteractionBehaviourCons cons) {
        this.behaviour = cons;
    }

    public InteractionBehaviourCons getBehaviour() {
        return behaviour;
    }
}



public class InteractionHandler implements Listener
{

    //when this inevitably gets too big we'll swap it out for a tree maybe
    //or a heap
    //or just burn the thing down and rebuild it from scratch
    private final Map<Material, List<InteractionBehaviourItem>> itemInteractions;
    private final Map<ConstructType,List<InteractionBehaviourCons>> constructInteractions;
    private final Map<IBHandle, InteractionBehaviour<?>> accessors;

    private static InteractionHandler instance = null;
    private final Logger logger;

    private InteractionHandler() {
        itemInteractions = new HashMap<>();
        accessors = new HashMap<>();
        constructInteractions = new HashMap<>();
        logger = FortressGuns.getInstance().getLogger();


        for (ItemBehaviour behaviour: ItemBehaviour.values()) {
            addItemBehaviour(behaviour.getBehaviour());
        }
    }

    public static InteractionHandler getInstance() {
        if (instance != null) return instance;
        instance = new InteractionHandler();
        return instance;
    }



    public void addItemBehaviour(InteractionBehaviourItem interaction) {

        IBHandle handle = interaction.getHandle();

        if (handle != null) {

            if (accessors.containsKey(handle))
                throw new IllegalArgumentException("Interaction handle "+handle.name()+" already registered!");
            accessors.put(handle, interaction);
        }


        Material[] labels = interaction.getLabels();
        for (Material mat: labels) {
            if (itemInteractions.containsKey(mat)) {
                List<InteractionBehaviourItem> behaviours = itemInteractions.get(mat);

                if (behaviours.contains(interaction)) {
                    throw new IllegalArgumentException("Interaction type already registered: "+interaction.getClass().getName());
                } else {
                    behaviours.add(interaction);
                }
            }
            else {
                List<InteractionBehaviourItem> list = new ArrayList<>();
                list.add(interaction);
                itemInteractions.put(mat, list);
            }
        }
    }


    public @Nullable InteractionBehaviourItem getItemBehaviour(IBHandle handle) {
        InteractionBehaviour<?> behaviour = accessors.getOrDefault(handle, null);
        if (behaviour == null)
            return null;

        if (behaviour instanceof InteractionBehaviourItem) return (InteractionBehaviourItem) behaviour;
        return null;
    }



    @EventHandler
    public void onPlayerScroll(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        ItemStack stack = player.getInventory().getItemInOffHand();
        Material mat = stack.getType();


        List<InteractionBehaviourItem> interactionBehaviour = itemInteractions.getOrDefault(mat, null);
        if (interactionBehaviour == null) return;

        Tuple2<Player, ItemStack> tup = new Tuple2<>(player, stack);
        for (InteractionBehaviourItem interaction: interactionBehaviour) {
            if (!interaction.accept(tup)) continue;
            interaction.onScroll(event);
        }

    }



    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        Material mat = stack.getType();

        List<InteractionBehaviourItem> interactions = itemInteractions.getOrDefault(mat, null);
        if (interactions == null) return;

        Tuple2<Player, ItemStack> tup = new Tuple2<>(event.getPlayer(), stack);
        for (InteractionBehaviourItem item: interactions) {
            if (!item.accept(tup)) continue;

            item.onBlockPlace(event);
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        ItemStack stack = event.getItem();
        Material mat = stack == null ? Material.AIR : stack.getType();

        List<InteractionBehaviourItem> interactions = itemInteractions.getOrDefault(mat, null);
        if (interactions == null) return;

        Consumer<InteractionBehaviourItem> itemAction = null;

        findAction:
        {
            if (event.getAction() == Action.LEFT_CLICK_AIR) {
                itemAction = behaviour -> behaviour.onLCAir(event);
                break findAction;
            }

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                itemAction = behaviour -> behaviour.onRCAir(event);
                break findAction;
            }

            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                itemAction = behaviour -> behaviour.onLCBlock(event);
                break findAction;
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                itemAction = behaviour -> behaviour.onRCBlock(event);
            }
        }

        if (itemAction == null)
            return;

        Tuple2<Player, ItemStack> tup = new Tuple2<>(event.getPlayer(), stack);

        for (InteractionBehaviourItem inter: interactions) {
            if (!inter.accept(tup)) continue;
            itemAction.accept(inter);
        }
    }

//
//    @EventHandler
//    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
//        Player player = event.getPlayer();
//
//        ItemStack main = player.getInventory().getItemInMainHand();
//        Material mat = main.getType();
//
//        List<InteractionBehaviourItem> interactionBehaviour = itemInteractions.getOrDefault(mat, null);
//        if (interactionBehaviour == null) return;
//
//        Tuple2<Player, ItemStack> tup = new Tuple2<>(player, main);
//        for (InteractionBehaviourItem interaction: interactionBehaviour) {
//            if (!interaction.accept(tup)) continue;
//            interaction.onRCEntity(event);
//        }
//    }



    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        ItemStack stack = event.getItem();
        Material mat = stack.getType();

        List<InteractionBehaviourItem> interactions =
                itemInteractions.getOrDefault(mat, null);

        if (interactions == null) return;

        Tuple2<Player, ItemStack> tup =
                new Tuple2<>(null, stack);

        for (InteractionBehaviourItem interaction : interactions) {
            if (!interaction.accept(tup)) continue;

            interaction.onDispense(event);
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack stack = event.getItem();
        Material mat = stack.getType();
        List<InteractionBehaviourItem> interactions =
                itemInteractions.getOrDefault(mat, null);
        if (interactions == null) return;
        Tuple2<Player, ItemStack> tup =
                new Tuple2<>(null, stack);
        for (InteractionBehaviourItem interaction : interactions) {
            if (!interaction.accept(tup)) continue;
            interaction.onItemConsume(event);
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack bow = event.getBow();

        if (bow == null) {
            return;
        }

        Material mat = bow.getType();

        List<InteractionBehaviourItem> interactions =
                itemInteractions.getOrDefault(mat, null);

        if (interactions == null) {
            return;
        }

        Tuple2<Player, ItemStack> tup =
                new Tuple2<>(player, bow);

        for (InteractionBehaviourItem interaction : interactions) {
            if (!interaction.accept(tup)) {
                continue;
            }

            interaction.onBowShoot(event);
        }
    }





    //-----------entity interactions----------------------------------





    public void addConstructBehaviour(InteractionBehaviourCons cons) {
        IBHandle handle = cons.getHandle();

        if (handle != null) {

            if (accessors.containsKey(handle))
                throw new IllegalArgumentException("Handle "+handle.name()+" already registered!");
            else {
                accessors.put(handle, cons);
            }
        }

        ConstructType[] types = cons.getLabels();
        for (ConstructType type: types) {
            List<InteractionBehaviourCons> interactions = constructInteractions.getOrDefault(type, null);


            if (interactions == null) {
                interactions = new ArrayList<>();
                interactions.add(cons);
                constructInteractions.put(type, interactions);
                continue;
            }

            if (interactions.contains(cons)) {
                throw new IllegalArgumentException("Construct Interaction is already registered: "+cons.getClass().getName());
            } else {
                interactions.add(cons);
            }
        }
    }









    //more of a global event
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


    //also more of a global event
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

        ConstructType type;
        Construct body;

        Component comp = ConstructUtils.getComponentRef(clicked);
        if (comp == null || comp.getBody() == null) return;

        body = comp.getBody();
        type = body.getType();

        ItemStack main = player.getInventory().getItemInMainHand();
        List<InteractionBehaviourCons> interactions = constructInteractions.getOrDefault(type, null);

        Tuple2<Player, ItemStack> tup = new Tuple2<>(player, main);

        for (InteractionBehaviourCons interaction: interactions) {
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

        Component comp = ConstructUtils.getComponentRef(damagee);
        if (comp == null) return;

        Construct struct = comp.getBody();
        ConstructType type = struct.getType();

        List<InteractionBehaviourCons> interactions = constructInteractions.getOrDefault(type, null);
        if (interactions == null) return;

        Tuple2<Player, ItemStack> tup = new Tuple2<>((Player) damager, main);

        for (InteractionBehaviourCons cons : interactions) {
            if (cons.accept(tup)) {
                cons.onLCCons(struct, comp, (Player)damager, main, event);
                return;   //this will only accept a single item
            }
        }
    }

}








