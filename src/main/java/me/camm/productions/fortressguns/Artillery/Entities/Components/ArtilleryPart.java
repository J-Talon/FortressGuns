package me.camm.productions.fortressguns.Artillery.Entities.Components;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties.AutoTracking;
import me.camm.productions.fortressguns.FortressGuns;
import net.minecraft.core.Vector3f;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import javax.annotation.Nullable;
import java.util.List;

public class ArtilleryPart extends Component
{
    protected Artillery body;
    protected final Material FIRE = Material.STICK;

    public ArtilleryPart(World world, Artillery body, double d0, double d1, double d2) {
        super(world, d0, d1, d2, body);
        this.body = body;
    }

    public ArtilleryPart(World world, Artillery body, Location loc){
        this(world, body, loc.getX(),loc.getY(),loc.getZ());
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
        if (body.isInvalid())
            return super.damageEntity(source,damage);
        else
        {
            Entity entity = source.getEntity();
            if (!(entity instanceof EntityPlayer)) {
                return damageRaw(source, damage);
            }

            EntityHuman human = ((EntityHuman)entity);
            List<Entity> riders;

            ArtilleryPart seat = body.getRotatingSeat();
            if (seat != null) {
                riders = seat.getPassengers();
            }
            else riders = body.getPivot().getPassengers();


            if (!riders.isEmpty()) {
                Entity e = riders.get(0);
                if (human.equals(e))
                    return false;
            }


            ItemStack holding = human.getItemInMainHand();

            org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asBukkitCopy(holding);

                //if they punch the thing with a stick, fire the cannon instead.
                Material mat = bukkitStack.getType();

                if (mat != FIRE) {
                    return damageRaw(source, damage);
                }

            if (source instanceof EntityDamageSource && source.u().equals("player")) {
                body.fire(new CraftPlayer(getWorld().getCraftServer(), (EntityPlayer)human));
                return false;
            }
            else return damageRaw(source, damage);





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

    public void seat(EntityHuman human) {

        ArtilleryPart seat = body.getRotatingSeat();
        Player bukkit = (CraftPlayer)human.getBukkitEntity();

        if (seat == null) {
            bukkit.sendMessage(ChatColor.RED+"No valid seat found!");
            return;
        }

        if (seat.getPassengers().size() > 0) {
            bukkit.sendMessage(ChatColor.RED+"Someone else is using this!");
            return;
        }

        if (body instanceof AutoTracking tracking) {
            if (tracking.isAiming()) {
              bukkit.sendMessage(ChatColor.RED+"You cannot operate this artillery while it's auto-tracking!");
                return;
            }
        }

        bukkit.sendMessage("Operating "+ ChatColor.RESET+body.getType().getName()+" Artillery.");
        bukkit.playSound(bukkit.getLocation(),Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.BLOCKS,1,1);

        body.setHasRider(true);
        human.startRiding(seat);

        new BukkitRunnable(){

            public void run()
            {

                if (body.isInvalid()) {
                    human.stopRiding();
                    body.setHasRider(false);
                    cancel();
                }

                Entity vehicle = human.getVehicle();
                if (vehicle!= null && vehicle.equals(seat)) {
                    body.pivot(Math.toRadians(human.getXRot()), Math.toRadians(human.getHeadRotation()));
                }
                else {
                    body.setHasRider(false);
                    cancel();
                }
            }
        }.runTaskTimer(FortressGuns.getInstance(),0,1);
    }



    /*

    Override of method for interaction
     */
    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand)
    {

        /*
                        a = success
                        b = consume
                        c = consume partial
                        d = pass
                        e = fail
        */

        ItemStack itemstack = entityhuman.b(enumhand);
        if (!this.isMarker() && !itemstack.a(Items.rQ)) {
            if (entityhuman.isSpectator()) {
                return EnumInteractionResult.a;
            } else if (entityhuman.t.y) {
                return EnumInteractionResult.b;
            } else {

                    handleInteraction(entityhuman, itemstack);
                    ArtilleryPart seat = body.getRotatingSeat();
                    if (seat == null) {
                        return EnumInteractionResult.b;
                    }
                    else {
                        List<Entity> pass = seat.getPassengers();
                        if (pass.isEmpty())
                            return EnumInteractionResult.b;

                        if (pass.get(0).equals(entityhuman))
                            return EnumInteractionResult.d;
                    }
            }
        } else {
            return EnumInteractionResult.d;
        }
        return EnumInteractionResult.b;
    }



    protected void handleInteraction(EntityHuman human, ItemStack stack) {

        if (stack == null) {
            seat(human);
            return;
        }

        org.bukkit.inventory.ItemStack bukkit = CraftItemStack.asBukkitCopy(stack);
        if (bukkit.getType() == Material.AIR || bukkit.getItemMeta() == null) {
            seat(human);
        }
    }

}
