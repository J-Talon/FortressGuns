package me.camm.productions.fortressguns.Core.Serialization;

import org.bukkit.inventory.ItemStack;

public interface IItemSerializable {

    //takes the construct and returns an fg box item
    abstract ItemStack toItemData();

    //
    abstract void loadItemData(ItemStack stack);
}
