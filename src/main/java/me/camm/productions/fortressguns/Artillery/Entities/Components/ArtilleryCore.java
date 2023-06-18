package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FlakArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyFlak;
import me.camm.productions.fortressguns.Handlers.InteractionHandler;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;

import java.util.UUID;

/*
Models the core of an artillery piece.
 */
public class ArtilleryCore extends ArtilleryPart {

    public ArtilleryCore(World world, Artillery body, double d0, double d1, double d2) {
        super(world, body, d0, d1, d2);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ai;
    }

    public Sound getSoundHurt(){
        return Sound.BLOCK_ANVIL_LAND;
    }

    @Override
    public boolean damageEntity(DamageSource source, float damage)
    {
        damage *= 2;
        return super.damageEntity(source, damage);
    }

    @Override
    public boolean startRiding(Entity e) {
        return false;
    }


    /*
    Note: may want to prevent this when the artillery is flak and it is aiming
     */
    public void seat(EntityHuman human){


        if (this.getPassengers().size() > 0) {
            human.sendMessage(new ChatMessage("Someone else is using this!"), UUID.randomUUID());
            return;
        }

        if (this.body instanceof FlakArtillery && ((FlakArtillery)body).getTarget() != null) {
            human.sendMessage(new ChatMessage("Cannot operate while artillery has a target!"), UUID.randomUUID());
            return;
        }

        ArtilleryPart seat = body.getRotatingSeat();
        if (seat != null) {
            seat.seat(human);
            human.sendMessage(new ChatMessage("Operating "+ ChatColor.RESET+body.getType().getName()+" Artillery."),UUID.randomUUID());
        }
    }


    //NMS methods that handle a player interacting with the armorstand.

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand hand)
    {
        //nms getting itemstack in the hand
        //enumHand is either the main or offhand
        //b() is getting the itemstack in the hand
        ItemStack selected = entityhuman.b(hand);

        if (entityhuman.isSpectator())
        {
            //a probably means cancelled or something
            return EnumInteractionResult.a;
        }

        handlePlayerInteract(entityhuman,selected);
        return EnumInteractionResult.a;
    }




    //this.a(entityhuman, enumitemslot2, itemstack, enumhand)
    /*
    When the player tries to put iron onto the stand, we cancel it and add health to the artillery, if possible.
     */
    public boolean handlePlayerInteract(EntityHuman human, ItemStack item) {

        if (!(human instanceof EntityPlayer)) {
            return handleUseInteraction(human);
        }

        org.bukkit.inventory.ItemStack stack = CraftItemStack.asBukkitCopy(item);

        if (stack.getType() != Material.STICK) {

            return handleUseInteraction(human);
        }

        if (!(body instanceof HeavyFlak))
            return false;



        HeavyFlak f = (HeavyFlak)body;
        org.bukkit.entity.Entity target = InteractionHandler.getTarget(human.getUniqueID());
        if (target == null) {
            return false;
        }

        Entity targetNMS = ((CraftEntity)target).getHandle();

        if (human.isCrouching()) {
            f.setTarget(null);
            f.setAiming(false);
            human.sendMessage(new ChatMessage(ChatColor.GRAY+"Stopped targeting."),UUID.randomUUID());
            return false;
        }

        boolean success = f.setTarget(targetNMS);
        if (!success) {
            human.sendMessage(new ChatMessage(ChatColor.GRAY+"Cannot aim at self!"),UUID.randomUUID());
        }
        else
            human.sendMessage(new ChatMessage(ChatColor.GRAY+"Aiming at target."),UUID.randomUUID());

        f.startAiming();

        return false;
    }



    public boolean handleUseInteraction(EntityHuman human) {


        // we first attempt to seat the player
        if (human.getVehicle()!=null) {
            return false;
        }

        if (getPassengers().size()!=0) {
            return false;
        }

        //if we cannot seat them, we try to open the inventory
        if (human.isCrouching()) {
            human.getBukkitEntity().openInventory(body.getInventory());
            //open an inventory here with the operations
            return false;
        }


        if (human instanceof EntityPlayer){


            //  (SoundEffect var0, SoundCategory var1, double var2, double var4, double var6, float var8, float var9)
            //sending a sound effect to the player.

                    /*
                    ai = arrow.hit.player
                    a = master
                     */

            seat(human);
            ((EntityPlayer)human).b.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.ai, SoundCategory.a,human.locX(),human.locY(), human.locZ(), 1f,1f));

        }

        return false;
    }
}
