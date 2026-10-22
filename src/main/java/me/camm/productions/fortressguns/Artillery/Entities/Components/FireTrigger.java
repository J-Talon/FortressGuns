package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FireTrigger extends ArtilleryPart {

    public FireTrigger(World world, Artillery body, Location loc) {
        super(world, body, loc);
    }


    @Override
    public void onRightClick(ItemStack mainHand, Player clicked) {
        super.onRightClick(mainHand, clicked);
    }


    @Override
    public boolean onLeftClick(ItemStack mainHand, Player clicked) {
        return super.onLeftClick(mainHand, clicked);
    }
}
