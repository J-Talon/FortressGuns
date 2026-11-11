package me.camm.productions.fortressguns.Handlers;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Util.EntityUtils;
import me.camm.productions.fortressguns.Artillery.Entities.Property.IRideable;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.interact.IBHandle;
import me.camm.productions.fortressguns.interact.InteractionBehaviour;
import me.camm.productions.fortressguns.interact.InteractionBehaviourItem;
import me.camm.productions.fortressguns.interact.ItemBehaviour.*;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Consumer;




enum ItemBehaviour {
    //DEV_SPYGLASS(new IBDevSpyglass()),  //DEV_SPYGLASS_TARGET
    DEV_BLAZEROD(new IBDevBlazeRod()),

    AMMO_ITEM(new IBAmmoItem()),
    CREATE_CONSTRUCT(new IBConstructBox()),
    RIDING_CONSTRUCT(new IBConstructs()),
    TACTICAL_PT(new IBTacticalPointer()), //TPOINTER_SETTING
    FLARE_GUN(new IBFlareGun()),
    FLARE(new IBFlare()),
    SIMPLE_INGREDIENT(new IBSimpleIngredient());

    private final InteractionBehaviourItem behaviour;

    private ItemBehaviour(InteractionBehaviourItem behaviour) {
        this.behaviour =  behaviour;
    }

    public InteractionBehaviourItem getBehaviour() {
        return behaviour;
    }
}


public class ItemInteractionHandler implements Listener
{
    //there are both pros and cons to using material
    //display names are freed up which mean that if people rename things they still work
    //but there is a performance penalty
    private final Map<Material, List<InteractionBehaviourItem>> itemInteractions;


    private final Map<IBHandle, InteractionBehaviour<?>> accessors;

    private static ItemInteractionHandler instance = null;

    private ItemInteractionHandler() {
        itemInteractions = new HashMap<>();
        accessors = new HashMap<>();

        for (ItemBehaviour behaviour: ItemBehaviour.values()) {
            addItemBehaviour(behaviour.getBehaviour());
        }

    }

    public static ItemInteractionHandler getInstance() {
        if (instance != null) return instance;
        instance = new ItemInteractionHandler();
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
        handleGeneric(player.getInventory().getItemInOffHand(),player, (e) -> e.onScroll(event));
    }



    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleGeneric(event.getItemInHand(), event.getPlayer(), (e) -> e.onBlockPlace(event));
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



    //item event
    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        handleGeneric(event.getItem(), null, (e) -> e.onDispense(event));
    }



    //item event
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        handleGeneric(event.getItem(), event.getPlayer(), (e) -> e.onItemConsume(event));
    }

    //item event
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

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Consumer<InteractionBehaviourItem> cons = (e) -> e.onPrepareCraft(event);
        for (ItemStack item : event.getInventory().getMatrix()) { // only 9 items so shouldn't be too bad
            handleGeneric(item, null,cons);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Consumer<InteractionBehaviourItem> cons = (e) -> e.onCraft(event);
        Player player = (Player)event.getWhoClicked();
        for (ItemStack item : event.getInventory().getMatrix()) {
            handleGeneric(item,player,cons);
        }
    }


    // ===================== furnaces =======================

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        handleGeneric(event.getFuel(), null, (e) -> e.onFurnaceBurn(event));
    }

    @EventHandler
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        handleGeneric(event.getSource(), null, (e) -> e.onFurnaceStartSmelt(event));
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack item = event.getSource();
        Tuple2<Player, ItemStack> tup = new Tuple2<>(null, item);
        List<InteractionBehaviourItem> interactions =
                itemInteractions.getOrDefault(item.getType(), null);

        if (interactions == null) return;

        for (InteractionBehaviourItem interaction : interactions) {
            if (!interaction.accept(tup)) continue;

            interaction.onFurnaceSmelt(event);
        }

        item = event.getResult();
        tup = new Tuple2<>(null, item);
        interactions = itemInteractions.getOrDefault(item.getType(), null);

        if (interactions == null) return;

        for (InteractionBehaviourItem interaction : interactions) {
            if (!interaction.accept(tup)) continue;

            interaction.onFurnaceSmelt(event);
        }
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) { // when output is taken out
        handleGeneric((ItemStack) event.getBlock(), event.getPlayer(), (e) -> e.onFurnaceExtract(event));
    }

    // ===================== furnaces =======================

    @EventHandler
    public void onBrew(BrewEvent event) {
        Consumer<InteractionBehaviourItem> cons = (e) -> e.onBrew(event);
        for (ItemStack item : event.getContents().getContents()) {
            handleGeneric(item, null, cons);
        }
    }

    @EventHandler
    public void onBrewingStandFuel(BrewingStandFuelEvent event) {
        handleGeneric(event.getFuel(), null, (e) -> e.onBrewingStandFuel(event));
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        for (ItemStack item : event.getInventory().getContents()) {
            handleGeneric(item,player,(e) -> e.onEnchantItem(event));
        }
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        Player player = event.getEnchanter();
        Consumer<InteractionBehaviourItem> cons = (e) -> e.onPrepareEnchant(event);
        for (ItemStack item : event.getInventory().getContents()) {
            handleGeneric(item, player, cons);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        Consumer<InteractionBehaviourItem> cons = (e) -> e.onPrepareAnvil(event);
        for (ItemStack item : event.getInventory().getContents()) {
            handleGeneric(item, null, cons);
        }
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        Consumer<InteractionBehaviourItem> cons = (e) -> e.onPrepareSmithing(event);
        for (ItemStack item : event.getInventory().getContents()) {
            handleGeneric(item,null, cons);
        }
    }

    @EventHandler
    public void onSmith(SmithItemEvent event) {
        Player player = (Player)event.getWhoClicked();
        Consumer<InteractionBehaviourItem> cons = (e) -> e.onSmith(event);
        for (ItemStack item : event.getInventory().getContents()) {
            handleGeneric(item, player, cons);
        }
    }





    private void handleGeneric(ItemStack item, Player player, Consumer<InteractionBehaviourItem> action) {
        if (item == null) return;
        List<InteractionBehaviourItem> interactions = itemInteractions.getOrDefault(item.getType(), null);
        if (interactions == null) return;
        Tuple2<Player, ItemStack> tup = new Tuple2<>(player, item);
        for (InteractionBehaviourItem interaction: interactions) {
            if (!interaction.accept(tup)) continue;
            action.accept(interaction);
        }
    }







    //-----------entity interactions----------------------------------




    //more of a global event, so I'm leaving it for now
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        org.bukkit.entity.Entity riding = player.getVehicle();

        if (riding == null)
            return;

        //whenever the player quits, the server creates a new entity whenever they join back. We do not want this to happen,
        //so we dismount them first.
        Component comp = EntityUtils.getComponentRef(riding);
        if (comp == null) return;

        player.leaveVehicle();

        if (comp.getBody() instanceof IRideable ride) {
            ride.onDismount(player);
        }
    }


    //also more of a global event. if this becomes a thing then
    //put it into the entity behaviour stuff
    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {

        org.bukkit.entity.Entity mount = event.getDismounted();
        Component comp = EntityUtils.getComponentRef(mount);
        if (comp == null) return;

        Construct cons = comp.getBody();
        Entity rider = event.getEntity();

        if (cons instanceof IRideable && rider.getType() == EntityType.PLAYER) {
            ((IRideable) cons).onDismount((Player) rider);
        }

    }

}








