package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Util.DamageSource.DamageMultiplier;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.core.Vector3f;

import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.decoration.EntityArmorStand;

import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.List;




public abstract class ComponentAS extends EntityArmorStand implements Component {

    protected Construct body;

    public ComponentAS(World world, double d0, double d1, double d2, Construct body) {
        super(world, d0, d1, d2);
        this.body = body;

    }


    @Override
    public @NotNull Construct getBody() {
        return body;
    }


    @Override
    public Entity getRider() {
        List<net.minecraft.world.entity.Entity> entities = this.getPassengers();
        if (entities == null || entities.isEmpty()) return null;
        return entities.get(0).getBukkitEntity();
    }

    @Override
    public void startRiding(Player player) {
        this.addPassenger(((CraftPlayer) player).getHandle());
        if (this.body instanceof Rideable rideable) {
            rideable.onMount(player);
        }
    }


    @Override
    public void teleport(double x, double y, double z) {
        this.teleportAndSync(x, y, z);
        this.g(x, y, z);
    }

    @Override
    public void teleport(@NotNull Location loc) {
        this.teleport(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public void setRotation(float x, float y) {
        this.setHeadPose(new Vector3f((float) Math.toDegrees(x), (float) Math.toDegrees(y), 0));
    }

    @Override
    public void setRotation(@NotNull EulerAngle angle) {
        setRotation((float) angle.getX(), (float) angle.getY());
    }


    public void setPose(Vector3f rightArm, Vector3f leftArm, Vector3f body, Vector3f rightLeg, Vector3f leftLeg) {

        super.setRightArmPose(rightArm);
        super.setLeftArmPose(leftArm);
        super.setBodyPose(body);
        super.setRightLegPose(rightLeg);
        super.setLeftLegPose(leftLeg);
    }

    //---------------------------
    //nms functions
    //------------------------------------------------------------------------------------------

    //is invisible
    @Override
    public boolean cx() {
        return false;
    }

    @Override
    public void remove() {
        this.die();
    }

    //cancel piston reactions
    @Override
    public EnumPistonReaction getPushReaction() {
        return EnumPistonReaction.d;
    }


    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        /*
            a = success = operation succeeded (placed / removed armor), pipeline ends
            b = consume = operation succeeded, no arm animation, pipeline ends
            c = consume partial = operation succeeded, no animation, no stats, pipeline ends
            d = pass = skip interaction, pipeline continues
            e = fail = item interaction failed, pipeline ends

            we should never return something that will allow the pipeline to continue; otherwise it will play spigot events
            which will mess stuff up

            for future me who will inevitably forget why I did this:
              spigot handles armor stand interact and damage events differently
              LCing a stand is registered as LCing air
        */

        //world.y is whether the world is clientside

        ItemStack item = CraftItemStack.asCraftMirror(entityhuman.b(enumhand));
        Player player = (Player) entityhuman.getBukkitEntity();

        if (this.isMarker() || item.getType() == Material.NAME_TAG)
            return EnumInteractionResult.d;

        if (player.getGameMode() == GameMode.SPECTATOR) {
            return EnumInteractionResult.a;
        }

        this.onRightClick(item, player);
        return EnumInteractionResult.c;
    }



    //It is probably better to handle the damage calculations in the components themselves, since
    //if we choose to do multiple version compatibility, nms will be different
    //there might not be an id, etc next time

    //oh and also; glowing.
    //yeah no glowing for you from spectral arrows
    protected boolean damageRaw(DamageSource source, float damage) {
        //String id = source.y;  //damagesource id

        if (source.isExplosion()) {
            damage *= DamageMultiplier.EXPLOSION.multiplier;
        }
        else if (source.isFire()) {
            damage *= DamageMultiplier.FIRE.multiplier;
        }
        else if (source instanceof GunSource) {   //tad slow
            damage *= DamageMultiplier.GUN.multiplier;
        }
        else if (source.isMagic()) {
            damage *= DamageMultiplier.MAGIC.multiplier;
        }
        else
            damage *= DamageMultiplier.DEFAULT.multiplier;

        return body.damage(damage);

    }


    @Override
    public boolean damageEntity(DamageSource source, float damage)
    {
        if (body.isInvalid())
            return super.damageEntity(source,damage);

        net.minecraft.world.entity.Entity entity = source.getEntity();
        if (entity == null) return damageRaw(source, damage);

        Entity bukkit = entity.getBukkitEntity();
        if (bukkit.getType() != EntityType.PLAYER) return damageRaw(source, damage);

        Player player = (Player) bukkit;
        boolean shouldDamage = onLeftClick(player.getInventory().getItemInMainHand(), player);

        return shouldDamage && damageRaw(source, damage);

    }
}








