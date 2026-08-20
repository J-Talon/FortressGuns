package me.camm.productions.fortressguns.interact.item.classification;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class FGItem<T> {

    //cache the generation result to save performance time
    //where possible
    protected ItemStack reference = null;

    protected abstract ItemStack generate();

    public final ItemStack get() {
        if (reference != null) return reference;
        reference = generate();
        return reference.clone();
    }

    //override this if necessary
    public ItemStack dynamicGet(@Nullable T context) {
        return get();
    }

    public abstract String getDisplayName();


    public boolean isSimilar(@Nullable ItemStack other) {
        ItemStack ref = get();
        if (ref == null) throw new IllegalStateException("Item generation returned null!");

        return ref.isSimilar(other);
    }

}
