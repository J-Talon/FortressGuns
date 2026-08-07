package me.camm.productions.fortressguns.Util.command;

import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.ArtilleryItems.ConstructItemHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGiveItems extends CommandHandler {


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be in-world to use this command");
            return true;
        }

        Inventory inv = player.getInventory();

        for (ConstructType type: ConstructType.values()) {
            ItemStack created = ConstructItemHelper.createArtilleryItem(type);
            inv.addItem(created);
        }

        for (AmmoItem item: AmmoItem.values()) {
            ItemStack ammo = ConstructItemHelper.createAmmoItem(item);
            inv.addItem(ammo);
        }

        inv.addItem(ConstructItemHelper.getStick());

        return true;
    }

    @Override
    public List<String> getTabCompletes(CommandSender sender, String[] in) {
        return List.of();
    }
}
