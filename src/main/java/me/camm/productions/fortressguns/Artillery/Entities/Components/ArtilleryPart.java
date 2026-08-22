package me.camm.productions.fortressguns.Artillery.Entities.Components;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.World;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ArtilleryPart extends ComponentAS
{
    protected Artillery body;
    public ArtilleryPart(World world, Artillery body, double d0, double d1, double d2) {
        super(world, d0, d1, d2, body);
        this.body = body;
    }

    public ArtilleryPart(World world, Artillery body, Location loc){
        this(world, body, loc.getX(),loc.getY(),loc.getZ());
    }


    public @NotNull Artillery getBody() {
        return body;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.Q;
    }

    public Sound getSoundHurt(){
        return Sound.BLOCK_BELL_USE;
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

}
