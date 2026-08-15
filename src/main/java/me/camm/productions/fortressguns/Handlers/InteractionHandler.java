package me.camm.productions.fortressguns.Handlers;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.FortressGuns;

import me.camm.productions.fortressguns.Util.chunk.ChunkLoader;
import me.camm.productions.fortressguns.item.interact.IBHandle;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviour;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import me.camm.productions.fortressguns.item.interact.behaviour.*;
import net.minecraft.server.level.EntityPlayer;

import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Consumer;




enum ItemBehaviour {
    AMMO_ITEM(new IBAmmoItem()),
    CREATE_CONSTRUCT(new IBConstructBox()),
    RIDING_CONSTRUCT(new IBConstructs()),
    DEV_SPYGLASS(new IBDevSpyglass()),
    TACTICAL_PT(new IBTacticalPointer()),
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



public class InteractionHandler implements Listener
{

    private final Map<Material, List<InteractionBehaviourItem>> itemInteractions;
    private final Map<IBHandle, InteractionBehaviour<?>> accessors;

    private static InteractionHandler instance = null;

    private InteractionHandler() {
        itemInteractions = new HashMap<>();
        accessors = new HashMap<>();


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

                if (behaviours.contains(interaction))
                    throw new IllegalArgumentException("Interaction type already registered!");
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






    //in the future I'd like to separate this out into it's own class
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

        for (InteractionBehaviourItem item: interactions) {
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

        for (InteractionBehaviourItem inter: interactions) {
            itemAction.accept(inter);
        }
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        ItemStack main = player.getInventory().getItemInMainHand();
        Material mat = main.getType();

        List<InteractionBehaviourItem> interactionBehaviour = itemInteractions.getOrDefault(mat, null);
        if (interactionBehaviour == null) return;

        Tuple2<Player, ItemStack> tup = new Tuple2<>(player, main);
        for (InteractionBehaviourItem interaction: interactionBehaviour) {
            if (!interaction.accept(tup)) continue;
            interaction.onRCEntity(event);
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
}
