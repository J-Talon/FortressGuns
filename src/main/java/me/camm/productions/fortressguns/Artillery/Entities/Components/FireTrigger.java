package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

import java.util.List;

public class FireTrigger extends ArtilleryPart {

    public FireTrigger(World world, Artillery body, Location loc) {
        super(world, body, loc);
    }


    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand)
    {

        if (!( entityhuman instanceof EntityPlayer))
            return EnumInteractionResult.d;


        if (!body.canFire())
            return EnumInteractionResult.d;

        if (body instanceof RapidFire) {


            RapidFire rapid = (RapidFire)body;
            List<Entity> entities = rapid.getRotatingSeat().getPassengers();

            if (entities.contains(entityhuman)) {
                body.fire(((EntityPlayer)entityhuman).getBukkitEntity());
            }

        }
        return EnumInteractionResult.d;
    }
}
