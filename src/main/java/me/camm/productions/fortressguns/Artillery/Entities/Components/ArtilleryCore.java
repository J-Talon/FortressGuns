package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.World;

import org.bukkit.Sound;

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


//
//    @Override
//    public boolean damageEntity(DamageSource source, float damage)
//    {
//        damage *= 2;
//        return super.damageEntity(source, damage);
//    }

    /*
    Note: may want to prevent this when the artillery is flak and it is aiming
     */


//    protected void handleInteraction(EntityHuman human, ItemStack item) {
//
//
//        Artillery arty = getBody();
//        if (arty instanceof Rideable ride) {
//            ComponentAS seat = ride.getSeat();
//            List<Entity> riders = seat.getPassengers();
//
//            if (!riders.isEmpty() && riders.get(0).equals(human)) {
//
//                if (arty.canFire()) {
//                    arty.fire((Player)human.getBukkitEntity());
//                    return;
//                }
//            }
//        }
//
//
//        org.bukkit.inventory.ItemStack stack = CraftItemStack.asBukkitCopy(item);
//        org.bukkit.inventory.ItemStack pointer = FGItems.TACTICAL_PTR.get();
//
//        if ((!pointer.isSimilar(stack))) {
//
//            if (human.isCrouching())
//                openMenu(human);
////            else
////                seat(human);
//
//            return;
//        }
//
//
//        if (human.isCrouching()) {
//
//            //I have some plans for this
//            //batteries anyone?
//            return;
//        }
//
//
//        if (!(body instanceof AutoTracking auto))
//            return;
//
//
//        org.bukkit.entity.Entity target = action.getTarget(human.getUniqueID());
//        if (target == null) {
//            return;
//        }
//
//        Entity targetNMS = ((CraftEntity)target).getHandle();
//        Player bukkit = ((EntityPlayer) human).getBukkitEntity();
//
//
//        if (auto.isAiming()) {
//            auto.setTarget(null);
//            auto.setAiming(false);
//            bukkit.sendMessage(ChatColor.GRAY+"Stopped targeting.");
//            return;
//        }
//
//        boolean success = auto.setTarget(targetNMS);
//        if (!success) {
//            bukkit.sendMessage(ChatColor.GRAY+"Cannot aim at self!");
//        }
//        else {
//
//            if (target.isDead() || (!target.isValid())) {
//                bukkit.sendMessage(ChatColor.GRAY+"Invalid target, already terminated.");
//                return;
//            }
//
//            bukkit.sendMessage(ChatColor.GRAY + "Aiming at target.");
//            auto.startAiming();
//        }
//    }





}
