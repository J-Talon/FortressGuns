package me.camm.productions.fortressguns.Artillery.Entities.Components;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.FortressGuns;
import net.minecraft.core.Vector3f;
import net.minecraft.server.level.EntityPlayer;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;


import javax.annotation.Nullable;
import java.util.List;

public class ArtilleryPart extends Component
{
    protected Artillery body;
    protected final Material FIRE = Material.STICK;
    protected boolean facesDown;

    public ArtilleryPart(World world, Artillery body, double d0, double d1, double d2) {
        super(world, d0, d1, d2, body);
        this.body = body;
        this.facesDown = false;
    }

    public ArtilleryPart(World world, Artillery body, Location loc){
        this(world, body, loc.getX(),loc.getY(),loc.getZ());
    }

    public boolean isFacesDown() {
        return facesDown;
    }

    public void setFacesDown(boolean facesDown) {
        this.facesDown = facesDown;
    }

    public Artillery getBody() {
        return body;
    }

    public void teleport( double x, double y, double z) {
        this.teleportAndSync(x,y,z);
        this.g(x,y,z);
    }

    public void teleport(Location loc) {
        this.teleport(loc.getX(), loc.getY(), loc.getZ());
    }

    public void setRotation(float x, float y){
        this.setHeadPose(new Vector3f((float)Math.toDegrees(x),(float)Math.toDegrees(y),0));
    }

    public void setRotation( EulerAngle angle) {
        setRotation((float)angle.getX(),(float)angle.getY());
    }


    public void setPose(Vector3f rightArm, Vector3f leftArm, Vector3f body, Vector3f rightLeg, Vector3f leftLeg){

        super.setRightArmPose(rightArm);
        super.setLeftArmPose(leftArm);
        super.setBodyPose(body);
        super.setRightLegPose(rightLeg);
        super.setLeftLegPose(leftLeg);
    }



    @Override
    public boolean damageEntity(DamageSource source, float damage)
    {
        if (body.inValid())
            return super.damageEntity(source,damage);
        else
        {
            Entity entity = source.getEntity();
            if (!(entity instanceof EntityPlayer)) {
                return damageRaw(source, damage);
            }

                EntityHuman human = ((EntityHuman)entity);

            List<Entity> riders;

            if (body instanceof RapidFire)
                riders = ((RapidFire)body).getRotatingSeat().getPassengers();
            else
                riders = body.getPivot().getPassengers();


            if (riders.size() != 0) {

                Entity e = riders.get(0);
                if (human.equals(e))
                    return false;
            }


                ItemStack holding = human.getItemInMainHand();

               org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asBukkitCopy(holding);

                //if they punch the thing with a stick, fire the cannon instead.
                Material mat = bukkitStack.getType();
                if (mat == FIRE) {
                    body.fire(new CraftPlayer(getWorld().getCraftServer(), (EntityPlayer)human));
                    return false;
                }
                else
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

    public void seat(EntityHuman human){

        if (!body.getType().isSeatable())
            return;

        ArtilleryCore core = body.getPivot();

        if (core.getPassengers().size() > 0 || this.getPassengers().size() > 0)
            return;


        human.startRiding(this);
        ArtilleryPart part = this;

        new BukkitRunnable(){

            public void run()
            {

                if (body.inValid()) {
                    human.stopRiding();
                    cancel();
                }

                Entity vehicle = human.getVehicle();
                if (vehicle!= null && vehicle.equals(part)) {
                    body.pivot(Math.toRadians(human.getXRot()), Math.toRadians(human.getHeadRotation()));
                }
                else
                    cancel();
            }
        }.runTaskTimer(FortressGuns.getInstance(),0,1);
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
                    EnumItemSlot enumitemslot1 = this.findInteractionArea(vec3d);
                    EnumItemSlot enumitemslot2 = this.d(enumitemslot1) ? enumitemslot : enumitemslot1;
                    if (this.a(enumitemslot2) && this.handleInteraction(entityhuman, enumitemslot2, itemstack)) {
                        return EnumInteractionResult.a;
                    }
                } else {
                    if (this.d(enumitemslot)) {
                        return EnumInteractionResult.e;
                    }

                    if (enumitemslot.a() == EnumItemSlot.Function.a && !this.hasArms()) {
                        return EnumInteractionResult.e;
                    }

                    if (this.handleInteraction(entityhuman, enumitemslot, itemstack)) {
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

    protected EnumItemSlot findInteractionArea(Vec3D vec3d) {
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


    private boolean handleInteraction(EntityHuman human, EnumItemSlot enumitemslot, ItemStack stack) {
       // ItemStack itemstack1 = this.getEquipment(enumitemslot);
        ArtilleryCore core = body.getPivot();
        return core.handlePlayerInteract(human,stack);


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










        //maybe open up an inventory here for loading and shooting?
    }
}
