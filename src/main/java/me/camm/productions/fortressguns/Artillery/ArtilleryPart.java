package me.camm.productions.fortressguns.Artillery;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;



import javax.annotation.Nullable;

public class ArtilleryPart extends EntityArmorStand
{
    protected final Artillery body;
    protected final Material FIRE = Material.STICK;

    public ArtilleryPart(EntityTypes<? extends EntityArmorStand> entitytypes, Artillery body, World world) {
        super(entitytypes, world);
        this.body = body;
    }

    public ArtilleryPart(World world, Artillery body, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
        this.body = body;
    }

    public ArtilleryPart(org.bukkit.World world, Artillery body, Location loc){
        this(((CraftWorld)world).getHandle(), body, loc.getX(),loc.getY(),loc.getZ());
    }

    public ArtilleryPart(World world, Artillery body, Location loc){
        this(world, body, loc.getX(),loc.getY(),loc.getZ());
    }

    @Override
    public boolean damageEntity(DamageSource source, float damage)
    {
        if (body.inValid())
            return super.damageEntity(source,damage);
        else
        {
            Entity entity = source.getEntity();
            if (entity instanceof EntityHuman) {
                EntityHuman human = ((EntityHuman)entity);
                ItemStack holding = human.getItemInMainHand();

               org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asBukkitCopy(holding);

                //if they punch the thing with a stick, fire the cannon instead.
                Material mat = bukkitStack.getType();
                if (mat == FIRE)
                    body.fire();
                else
                    return damageRaw(source, damage);
            }

            return damageRaw(source, damage);
        }

    }

    private boolean damageRaw(DamageSource source, float damage){
        body.playSound(this);
        return body.damage(source, damage);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.Q;
    }

    public Sound getSoundHurt(){
        return Sound.BLOCK_BELL_USE;
    }

    public void setLocation(double x, double y, double z){
        g(x,y,z);
    }

    public Location getLocation(org.bukkit.World world){
        return new Location(world,u,v,w);
    }

    public Location getEyeLocation(){
        return this.toBukkit().getEyeLocation();
    }

    protected ArmorStand toBukkit(){
        return (ArmorStand)this.getBukkitEntity();
    }


    @Nullable
    protected SoundEffect getSoundDeath() {
        return SoundEffects.gJ;
    }



    /*

    Override of method for interaction
     */
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
                    if (this.a(enumitemslot2) && this.a(entityhuman, enumitemslot2, itemstack, enumhand)) {
                        return EnumInteractionResult.a;
                    }
                } else {
                    if (this.d(enumitemslot)) {
                        return EnumInteractionResult.e;
                    }

                    if (enumitemslot.a() == EnumItemSlot.Function.a && !this.hasArms()) {
                        return EnumInteractionResult.e;
                    }

                    if (this.a(entityhuman, enumitemslot, itemstack, enumhand)) {
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


    private boolean a(EntityHuman entityhuman, EnumItemSlot enumitemslot, ItemStack itemstack, EnumHand enumhand) {
        ItemStack itemstack1 = this.getEquipment(enumitemslot);

        //if the itemstack is not empty and something bit shifting + 8 is not 0,
        /*
        These probably mean: whether or not the itemstack to place in is empty or not, and if the slot is locked, then
        return false

        we should probably return false for all cases here actually.
         */

        /*
        if (!itemstack1.isEmpty() && (this.cf & 1 << enumitemslot.getSlotFlag() + 8) != 0) {
            return false;
        }

        if (itemstack1.isEmpty() && (this.cf & 1 << enumitemslot.getSlotFlag() + 16) != 0)
        {
            return false;
        }

         */
        double health = body.getHealth();
        health *= 1000;
        health = Math.round(health);
        health /= 1000;
        entityhuman.sendMessage(new ChatMessage("This artillery is on "+health+" Hp"),entityhuman.getUniqueID());



        //maybe open up an inventory here for loading and shooting?



        return false;
    }
}
