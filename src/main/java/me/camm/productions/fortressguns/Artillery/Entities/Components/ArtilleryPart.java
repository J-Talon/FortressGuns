package me.camm.productions.fortressguns.Artillery.Entities.Components;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Property.AutoTracking;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.interact.item.ItemUtils;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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



//
//    @Override
//    public boolean damageEntity(DamageSource source, float damage)
//    {
//        if (body.isInvalid())
//            return super.damageEntity(source,damage);
//        else
//        {
//            Entity entity = source.getEntity();
//            if (!(entity instanceof EntityPlayer)) {
//                return damageRaw(source, damage);
//            }
//
//            EntityHuman human = ((EntityHuman)entity);
//            List<org.bukkit.entity.Entity> riders = new ArrayList<>();
//
//
//            if (body instanceof Rideable rideable) {
//                List<Entity> nmsRiders = rideable.getSeat().getPassengers();
//                for (Entity nms: nmsRiders) {
//                    riders.add(nms.getBukkitEntity());
//                }
//            }
//            else riders = body.getCoreEntity().getPassengers();
//
//
//            if (!riders.isEmpty()) {
//                org.bukkit.entity.Entity e = riders.get(0);
//                if (human.equals(e))
//                    return false;
//            }
//
//
//            ItemStack holding = human.getItemInMainHand();
//
//            org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asBukkitCopy(holding);
//            org.bukkit.inventory.ItemStack pointer = FGItems.TACTICAL_PTR.get();
//
//                //if they punch the thing with a stick, fire the cannon instead.
//
//
//                if (!(pointer.isSimilar(bukkitStack))) {
//                    return damageRaw(source, damage);
//                }
//
//            if (source instanceof EntityDamageSource && source.u().equals("player")) {
//                body.fire(new CraftPlayer(getWorld().getCraftServer(), (EntityPlayer)human));
//                return false;
//            }
//            else return damageRaw(source, damage);
//
//
//        }
//
//    }




//
//    private boolean damageRaw(DamageSource source, float damage){
//        body.playSound(this);
//        return body.damage(source, damage);
//    }

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
