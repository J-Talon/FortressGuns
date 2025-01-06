package me.camm.productions.fortressguns.Inventory.Abstract;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Handlers.InventoryHandler;
import me.camm.productions.fortressguns.Inventory.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class InventoryGroup {

    private final Map<String, ConstructInventory> inventories;
    private final Map<InventoryCategory, String> tags;
    protected Construct owner;

    private final static Map<Class<? extends ConstructInventory>, String> registeredInventories = new HashMap<>();

    static {
        for (InventoryId name: InventoryId.values()) {
            register(name);
        }
    }

    public static void register(InventoryId name) {
        if (registeredInventories.containsValue(name.getName()) || registeredInventories.containsKey(name.getInv()))
            throw new IllegalArgumentException("Inventory already registered: "+name.getName());
        registeredInventories.put(name.getInv(), name.getName());
    }


    public InventoryGroup(Construct owner) {
        inventories = new HashMap<>();
        tags = new HashMap<>();

        this.owner = owner;
        init();
    }


    public ConstructInventory getInventory(String name) {
        return inventories.getOrDefault(name, null);
    }

    public void openInventory(InventoryCategory cat, Player player) {
       ConstructInventory inv = getInventoryByCategory(cat);
       openInventory(inv, player);
    }


    public void openInventory(Player player, String name) {
        ConstructInventory inv = getInventory(name);
        if (inv == null)
            return;

        InventoryHandler.startInteraction(player,inv);
    }

    public void openInventory(ConstructInventory inv, Player player) {
        InventoryHandler.startInteraction(player,inv);
    }

    public @Nullable ConstructInventory getInventoryByCategory(InventoryCategory cat) {
        String id = tags.getOrDefault(cat, null);
        if (id == null)
            return null;

        //if this returns null then technically it means there's some
        //desynchronization
        return inventories.getOrDefault(id, null);
    }

    public void addInventory(ConstructInventory inv) {

        Class<? extends ConstructInventory> cl = inv.getClass();
        String id = registeredInventories.getOrDefault(cl, null);
        if (id == null)
            throw new IllegalStateException("Inventory was not registered before adding to group:"+ cl);

        InventoryCategory cat = inv.getTag();


        if (tags.containsKey(cat))
            throw new IllegalArgumentException("Inventory group already has an inventory for "+cat);

        inventories.put(id, inv);
        tags.put(cat, id);
    }

    protected abstract void init();



   public static class StandardGroup extends InventoryGroup {
        public StandardGroup(Artillery owner) {
            super(owner);
        }
        @Override
        protected void init() {
            addInventory(new StandardLoadingInventory((Artillery)owner,this));
            addInventory(new PrecisionMenuInventory((Artillery)owner,this));
        }
    }



   public static class RapidGroup extends InventoryGroup {
        public RapidGroup(Artillery owner) {
            super(owner);
        }

        @Override
        protected void init() {
            addInventory(new BulkLoadingInventory((Artillery)owner,this));
            addInventory(new JamInventory((RapidFire)owner, this));
            addInventory(new RoughMenuInventory((Artillery)owner, this));
        }
    }





}
