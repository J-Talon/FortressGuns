package me.camm.productions.fortressguns.Artillery;

import me.camm.productions.fortressguns.FortressGuns;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ArtilleryCore extends ArtilleryPart {

    public ArtilleryCore(EntityTypes<? extends EntityArmorStand> entitytypes, Artillery body, World world) {
        super(entitytypes, body, world);
    }

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


    /*
    Note: may want to prevent this when the artillery is flak and it is aiming
     */
    public void seat(EntityHuman human){
        if (body.getType() == ArtilleryType.MISSILE)
            return;

        human.startRiding(this);
        ArtilleryCore core = this;

        new BukkitRunnable(){

            public void run()
            {

                if (body.inValid()) {
                    human.stopRiding();
                    cancel();
                }

                if (human.getVehicle() != null && human.getVehicle().equals(core)) {
                    body.pivot(Math.toRadians(Math.min(human.getXRot(),0)), Math.toRadians(human.getHeadRotation()));
                }
                else
                    cancel();
            }
        }.runTaskTimer(FortressGuns.getInstance(),0,1);
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand)
    {
        ItemStack itemstack = entityhuman.b(enumhand);
        if (!this.isMarker() && !itemstack.a(Items.rQ)) {
            if (entityhuman.isSpectator()) {
                return EnumInteractionResult.a;
            } else if (entityhuman.t.y) {
                return EnumInteractionResult.b;
            } else {
                EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
                if (itemstack.isEmpty()) {
                    EnumItemSlot enumitemslot1 = this.i(vec3d);
                    EnumItemSlot enumitemslot2 = this.d(enumitemslot1) ? enumitemslot : enumitemslot1;
                    if (this.a(enumitemslot2) && this.seatPlayer(entityhuman, enumitemslot2, itemstack)) {
                        return EnumInteractionResult.a;
                    }
                } else {
                    if (this.d(enumitemslot)) {
                        return EnumInteractionResult.e;
                    }

                    if (enumitemslot.a() == EnumItemSlot.Function.a && !this.hasArms()) {
                        return EnumInteractionResult.e;
                    }

                    if (this.seatPlayer(entityhuman, enumitemslot, itemstack)) {
                        return EnumInteractionResult.a;
                    }
                }

                return EnumInteractionResult.d;
            }
        } else {
            return EnumInteractionResult.d;
        }
    }

    private boolean d(EnumItemSlot enumitemslot) {
        return (this.cf & 1 << enumitemslot.getSlotFlag()) != 0 || enumitemslot.a() == EnumItemSlot.Function.a && !this.hasArms();
    }

    private EnumItemSlot i(Vec3D vec3d) {
        EnumItemSlot enumitemslot = EnumItemSlot.a;
        boolean flag = this.isSmall();
        double d0 = flag ? vec3d.c * 2.0D : vec3d.c;
        EnumItemSlot enumitemslot1 = EnumItemSlot.c;
        if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.a(enumitemslot1)) {
            enumitemslot = EnumItemSlot.c;
        } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.a(EnumItemSlot.e)) {
            enumitemslot = EnumItemSlot.e;
        } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.a(EnumItemSlot.d)) {
            enumitemslot = EnumItemSlot.d;
        } else if (d0 >= 1.6D && this.a(EnumItemSlot.f)) {
            enumitemslot = EnumItemSlot.f;
        } else if (!this.a(EnumItemSlot.a) && this.a(EnumItemSlot.b)) {
            enumitemslot = EnumItemSlot.b;
        }

        return enumitemslot;
    }


    //this.a(entityhuman, enumitemslot2, itemstack, enumhand)
    private boolean seatPlayer(EntityHuman human, EnumItemSlot slot, ItemStack item) {

        if (item != null) {
            org.bukkit.inventory.ItemStack stack = CraftItemStack.asBukkitCopy(item);
            Material mat = stack.getType();
            if (mat==Material.IRON_INGOT) {

                if (body.getHealth() >= body.getMaxHealth()) {
                    human.sendMessage(new ChatMessage("Artillery is already at max Hp."),human.getUniqueID());
                    return false;
                }

                if (item.getCount()==1)
                    item = null;
                else
                    item.setCount(item.getCount()-1);

                human.setSlot(slot, item);

                body.setHealth(Math.min(body.getHealth()+5,body.getMaxHealth()));
                human.sendMessage(new ChatMessage("Repaired artillery. (Now is at "+body.getHealth()+" Hp)"),human.getUniqueID());
                human.playSound(SoundEffects.T,1,1);
            }
            return false;
        }

        if (human.getVehicle()==null) {
            if (getPassengers().size()==0) {
                seat(human);
                human.sendMessage(new ChatMessage("Operating "+ ChatColor.RESET+body.getType().getName()+" Artillery."),human.getUniqueID());
            }
        }

        return false;
    }
}
