package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import net.minecraft.core.Vector3f;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.EnumPistonReaction;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComponentAS extends EntityArmorStand implements Component {

    protected Construct body;
    public ComponentAS(World world, double d0, double d1, double d2, Construct body) {
        super(world, d0, d1, d2);
        this.body = body;

    }



    @Override
    public EnumPistonReaction getPushReaction() {
        return EnumPistonReaction.d;
    }

    @Override
    public @NotNull Construct getBody() {
        return body;
    }

    @Override
    public boolean cx() {
        return false;
    }

    @Override
    public void remove() {
        this.die();
    }

    @Override
    public Entity getRider() {
        List<net.minecraft.world.entity.Entity> entities = this.getPassengers();
        if (entities == null || entities.isEmpty()) return null;
        return entities.get(0).getBukkitEntity();
    }

    @Override
    public void startRiding(Player player) {
        this.addPassenger(((CraftPlayer)player).getHandle());
        if (this.body instanceof Rideable rideable) {
            rideable.onMount(player);
        }
    }



    @Override
    public void teleport(double x, double y, double z) {
        this.teleportAndSync(x,y,z);
        this.g(x,y,z);
    }

    @Override
    public void teleport(@NotNull Location loc) {
        this.teleport(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public void setRotation(float x, float y) {
        this.setHeadPose(new Vector3f((float)Math.toDegrees(x),(float)Math.toDegrees(y),0));
    }

    @Override
    public void setRotation(@NotNull EulerAngle angle) {
        setRotation((float)angle.getX(),(float)angle.getY());
    }


    public void setPose(Vector3f rightArm, Vector3f leftArm, Vector3f body, Vector3f rightLeg, Vector3f leftLeg){

        super.setRightArmPose(rightArm);
        super.setLeftArmPose(leftArm);
        super.setBodyPose(body);
        super.setRightLegPose(rightLeg);
        super.setLeftLegPose(leftLeg);
    }




}
