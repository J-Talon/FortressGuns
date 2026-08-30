package me.camm.productions.fortressguns.Artillery.Entities.Components;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.interact.item.ItemUtils;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.World;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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


    @Override
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


    @Override
    public boolean onLeftClick(ItemStack mainHand, Player clicked) {

        if (body instanceof Rideable ride && ride.getRider() != null) {
            if (ride.getRider().getUniqueId().equals(clicked.getUniqueId())) {
                body.fire(clicked);
                return false;
            }
        }

        if (FGItems.TACTICAL_PTR.isSimilar(mainHand)) {
            body.fire(clicked);
            return false;
        }

        return true;
    }

    @Override
    public void onRightClick(ItemStack mainHand, Player clicked) {

        if (body instanceof Rideable ride && ride.getRider() != null) {
            if (ride.getRider().getUniqueId().equals(clicked.getUniqueId())) {
                body.fire(clicked);
            }


        }
    }
}
