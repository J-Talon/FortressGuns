package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import org.bukkit.Location;

import java.util.List;

public class FireTrigger extends ArtilleryPart {

    public FireTrigger(World world, Artillery body, Location loc) {
        super(world, body, loc);
    }

    @Override
    protected void handleInteraction(EntityHuman human, ItemStack stack) {


        if (!( human instanceof EntityPlayer))
            return;

        if (body instanceof RapidFire rapid) {

            List<Entity> entities = rapid.getRotatingSeat().getPassengers();

            if (entities.size() > 0) {
                if (!body.canFire() || !(entities.get(0).equals(human)))
                    return;

                body.fire(((EntityPlayer)human).getBukkitEntity());
            }
            else {
                seat(human);
            }
        }



    }
}
