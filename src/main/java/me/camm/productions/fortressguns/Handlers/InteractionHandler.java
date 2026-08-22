package me.camm.productions.fortressguns.Handlers;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Util.Math.Tuple2;

import me.camm.productions.fortressguns.interact.IBHandle;
import me.camm.productions.fortressguns.interact.InteractionBehaviour;
import me.camm.productions.fortressguns.interact.InteractionBehaviourCons;
import me.camm.productions.fortressguns.interact.InteractionBehaviourItem;
import me.camm.productions.fortressguns.interact.behaviour.ConstructBehaviour.CBGlobalInteractStanding;
import me.camm.productions.fortressguns.interact.behaviour.ConstructBehaviour.CBGlobalPointer;
import me.camm.productions.fortressguns.interact.behaviour.ConstructBehaviour.CBGlobalRiding;
import me.camm.productions.fortressguns.interact.behaviour.ItemBehaviour.*;

import org.bukkit.*;
import org.bukkit.entity.Entity;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Consumer;


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
    GLOBAL_STANDING(new CBGlobalInteractStanding()),
    GLOBAL_POINTER(new CBGlobalPointer()),
    GLOBAL_RIDING(new CBGlobalRiding());

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
    //there are both pros and cons to using material
    //display names are freed up which mean that if people rename things they still work
    //but there is a performance penalty
    private final Map<Material, List<InteractionBehaviourItem>> itemInteractions;
    private final Map<ConstructType,Map<Material, List<InteractionBehaviourCons>>> constructInteractions;
    private final List<InteractionBehaviourCons> wildcards;

    private final Map<IBHandle, InteractionBehaviour<?>> accessors;

    private static InteractionHandler instance = null;

    private InteractionHandler() {
        itemInteractions = new HashMap<>();
        accessors = new HashMap<>();
        constructInteractions = new HashMap<>();
        wildcards = new ArrayList<>();



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

    private void generateEntry(Material mat, @NotNull Map<Material, List<InteractionBehaviourCons>> innerMap, InteractionBehaviourCons interaction) {

        List<InteractionBehaviourCons> consList;
        if (innerMap.containsKey(mat)) {
             consList = innerMap.get(mat);
            if (consList.contains(interaction)) throw new IllegalArgumentException("Interaction "+interaction.getClass().getName()+" already registered!");
            consList.add(interaction);
        }
        else {
            consList = new ArrayList<>();
            consList.add(interaction);
            innerMap.put(mat, consList);
        }
    }


    public void addConstructBehaviour(InteractionBehaviourCons interaction) {
        IBHandle handle = interaction.getHandle();

        if (handle != null) {

            if (accessors.containsKey(handle))
                throw new IllegalArgumentException("Handle "+handle.name()+" already registered!");
            else {
                accessors.put(handle, interaction);
            }
        }

        if (interaction.treatGlobally()) {

            if (wildcards.contains(interaction))
                throw new IllegalArgumentException(interaction.getClass().getName() +" is already registered as a wildcard!");
            wildcards.add(interaction);
            return;
        }

        ConstructType[] types = interaction.getPrimaryLabels();
        Material[] secondaryLabels = interaction.getSecondaryLabels();


        for (ConstructType type: types) {
            Map<Material,List<InteractionBehaviourCons>> inner = constructInteractions.getOrDefault(type, null);
            if (inner == null)
                inner = new HashMap<>();

            for (Material mat: secondaryLabels) {
                this.generateEntry(mat, inner, interaction);
            }
        }
    }









    //more of a global event, so I'm leaving it for now
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        org.bukkit.entity.Entity riding = player.getVehicle();

        if (riding == null)
            return;

        //whenever the player quits, the server creates a new entity whenever they join back. We do not want this to happen,
        //so we dismount them first.
        Component comp = ConstructUtils.getComponentRef(riding);
        if (comp == null) return;

        player.leaveVehicle();

        if (comp.getBody() instanceof Rideable ride) {
            ride.onDismount(player);
        }
    }


    //also more of a global event. if this becomes a thing then
    //put it into the entity behaviour stuff
    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {

        org.bukkit.entity.Entity mount = event.getDismounted();
        Component comp = ConstructUtils.getComponentRef(mount);
        if (comp == null) return;

        Construct cons = comp.getBody();
        Entity rider = event.getEntity();

        if (cons instanceof Rideable && rider.getType() == EntityType.PLAYER) {
            ((Rideable) cons).onDismount((Player) rider);
        }

    }




    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        org.bukkit.entity.Entity clicked = event.getRightClicked();

        ConstructType type;
        Construct body;

        Component comp = ConstructUtils.getComponentRef(clicked);
        if (comp == null) return;

        body = comp.getBody();
        type = body.getType();
        ItemStack main = player.getInventory().getItemInMainHand();

        Tuple2<Player, ItemStack> tup = new Tuple2<>(player, main);


        for (InteractionBehaviourCons wc: wildcards) {
            if (wc.accept(tup)) {
                wc.onRCCons(body, comp, main, event);
            }
        }

        Map<Material,List<InteractionBehaviourCons>> inner = constructInteractions.getOrDefault(type, null);
        if (inner == null) return;

        List<InteractionBehaviourCons> interactions = inner.getOrDefault(main.getType(), null);
        if (interactions == null) return;

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
        Player player = (Player)damager;

        Tuple2<Player, ItemStack> tup = new Tuple2<>(player, main);

        for (InteractionBehaviourCons wc: wildcards) {
            if (wc.accept(tup)) {
                wc.onLCCons(struct, comp, player, main, event);
            }
        }

        Map<Material, List<InteractionBehaviourCons>> inner = constructInteractions.getOrDefault(type, null);
        if (inner == null) return;

        List<InteractionBehaviourCons> interactions = inner.getOrDefault(main.getType(), null);
        if (interactions == null) return;

        for (InteractionBehaviourCons cons : interactions) {
            if (cons.accept(tup)) {
                cons.onLCCons(struct, comp, player, main, event);
            }
        }
    }

}








