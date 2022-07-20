package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyMachineGun;
import me.camm.productions.fortressguns.FortressGuns;
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
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import org.bukkit.scheduler.BukkitRunnable;

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

        if (!body.getType().isSeatable())
            return;

        if (this.getPassengers().size() > 0)
            return;


        if (body instanceof RapidFire) {
            ArtilleryPart seat = ((HeavyMachineGun) body).getRotatingSeat();
            seat.seat(human);
            return;
        }



        human.startRiding(this);
        ArtilleryCore core = this;


        new BukkitRunnable(){

            public void run()
            {

                if (body.inValid()) {
                    human.stopRiding();
                    cancel();
                }

                Entity vehicle = human.getVehicle();
                if (vehicle!= null && vehicle.equals(core)) {
                    body.pivot(Math.toRadians(Math.min(human.getXRot(),0)), Math.toRadians(human.getHeadRotation()));
                }
                else
                    cancel();
            }
        }.runTaskTimer(FortressGuns.getInstance(),0,1);
    }


    //NMS methods that handle a player interacting with the armorstand.

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand hand)
    {
        //nms getting itemstack in the hand
        //enumHand is either the main or offhand
        //b() is getting the itemstack in the hand
        ItemStack selected = entityhuman.b(hand);

        //if the stand is not a marker, and the itemstack is not equal to a nametag
        if (!this.isMarker() && !selected.a(Items.rQ))
        {
            if (entityhuman.isSpectator())
            {
                //a probably means cancelled or something
                return EnumInteractionResult.a;
            }

                //it tries to put or remove the itemstack from the stand...?
                EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(selected);
                if (selected.isEmpty()) {
                    EnumItemSlot enumitemslot1 = findInteractionArea(vec3d);
                    EnumItemSlot enumitemslot2 = this.checkSlots(enumitemslot1) ? enumitemslot : enumitemslot1;
                    if (this.a(enumitemslot2) && this.handlePlayerInteract(entityhuman, selected)) {
                        return EnumInteractionResult.a;
                    }
                } else {
                    if (this.checkSlots(enumitemslot)) {
                        return EnumInteractionResult.e;
                    }

                    if (enumitemslot.a() == EnumItemSlot.Function.a && !this.hasArms()) {
                        return EnumInteractionResult.e;
                    }

                    //so this is where it tries to put the thing onto the hand. In this case, we override and cancel it.
                    if (this.handlePlayerInteract(entityhuman, selected)) {
                        return EnumInteractionResult.a;
                    }
                }

        }
        return EnumInteractionResult.d;
    }


    //no idea what this does. Probably checking the slots
    private boolean checkSlots(EnumItemSlot enumitemslot) {

        //bit shifting to the right?
        return (this.cf & 1 << enumitemslot.getSlotFlag()) != 0 || enumitemslot.a() == EnumItemSlot.Function.a && !this.hasArms();
        //a and b are probably the main and offhand
    }



    //this.a(entityhuman, enumitemslot2, itemstack, enumhand)
    /*
    When the player tries to put iron onto the stand, we cancel it and add health to the artillery, if possible.
     */
    public boolean handlePlayerInteract(EntityHuman human, ItemStack item) {


        if (!item.isEmpty())
            return false;

        // we first attempt to seat the player
        if (human.getVehicle()!=null) {
            return false;
        }

        if (getPassengers().size()!=0) {
            return false;
        }

        //if we cannot seat them, we try to open the inventory
        if (human.isCrouching()) {
            human.sendMessage(new ChatMessage("[DEBUG] - Open inventory. (Not implemented yet)"),UUID.randomUUID());
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
             human.sendMessage(new ChatMessage("Operating "+ ChatColor.RESET+body.getType().getName()+" Artillery."),UUID.randomUUID());
         }






        return false;
    }
}
