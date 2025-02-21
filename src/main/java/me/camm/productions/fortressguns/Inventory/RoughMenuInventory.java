package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Inventory.Abstract.*;
import org.bukkit.inventory.ItemStack;

public class RoughMenuInventory extends MenuInventory {


    static ItemStack reload, disassemble, border;
    static {
        reload = StaticItem.RELOAD.toItemRaw();
        disassemble = StaticItem.DISASSEMBLE.toItemRaw();
        border = StaticItem.BORDER.toItemRaw();
    }


    public RoughMenuInventory(Artillery owner, InventoryGroup group) {
        super(owner, group);
        functions.put(StaticItem.RELOAD.getName(), openReloading);
        functions.put(StaticItem.DISASSEMBLE.getName(), MenuInventory.disassemble);
        init();
    }

    @Override
    public void init() {
        //[] [][][] [] [][][] []
        for (int i = 1; i < 5; i ++) {
            gui.setItem(9 * i + 1, reload);
            gui.setItem(9 * i + 2, reload);
            gui.setItem(9 * i + 3, reload);

            gui.setItem(9 * i + 5, disassemble);
            gui.setItem(9 * i + 6, disassemble);
            gui.setItem(9 * i + 7, disassemble);
        }

        for (int i = 0; i < 6; i ++) {
            gui.setItem(9 * i, border);
            gui.setItem(9 * i + 8 ,border);
        }

    }


    @Override
    protected boolean isStaticItem(ItemStack current) {
        return reload.isSimilar(current) || disassemble.isSimilar(current) || border.isSimilar(current);
    }
}
