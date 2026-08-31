package me.camm.productions.fortressguns.Util.command;

import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import me.camm.productions.fortressguns.interact.item.classification.box.FGBoxItem;
import me.camm.productions.fortressguns.interact.item.classification.ingredients.FGSimpleIngredient;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.camm.productions.fortressguns.Util.command.PermissionNodeLabel.FG_ADMIN;
import static me.camm.productions.fortressguns.interact.item.ItemUtils.*;

public class CommandGiveItems extends CommandHandler {


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command");
            return true;
        }

        Inventory inv = player.getInventory();

        for (ConstructType type: ConstructType.values()) {
            FGBoxItem box = type.getBoxItem();
            if (box == null) continue;
            inv.addItem(box.get());
        }

        for (AmmoItem item: AmmoItem.values()) {
            ItemStack ammo = createAmmoItem(item);
            inv.addItem(ammo);
        }

        for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
            inv.addItem(ingredient.get());
        }

        inv.addItem(FGItems.TACTICAL_PTR.get());
        inv.addItem(FGItems.FLARE_GUN.get());

        return true;
    }

    @Override
    public List<String> getTabCompletes(CommandSender sender, String[] in) {
        return List.of();
    }

    @Override
    public String getPermissionNode() {
        return FG_ADMIN.label();
    }
}
