package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties.AutoTracking;
import me.camm.productions.fortressguns.Handlers.InteractionHandler;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import org.bukkit.ChatColor;

import org.bukkit.Sound;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.UUID;

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

    @Override
    public boolean damageEntity(DamageSource source, float damage)
    {
        damage *= 2;
        return super.damageEntity(source, damage);
    }

    @Override
    public boolean startRiding(Entity e) {
        return false;
    }


    /*
    Note: may want to prevent this when the artillery is flak and it is aiming
     */


    protected void handleInteraction(EntityHuman human, ItemStack item) {

        if (!(human instanceof EntityPlayer)) {
            openMenu(human);
            return;
        }

        org.bukkit.inventory.ItemStack stack = CraftItemStack.asBukkitCopy(item);

        if (stack.getType() != FIRE) {

            if (human.isCrouching())
                openMenu(human);
            else
                seat(human);

            return;
        }

        if (!(body instanceof AutoTracking auto))
            return;


        org.bukkit.entity.Entity target = InteractionHandler.getTarget(human.getUniqueID());
        if (target == null) {
            return;
        }

        Entity targetNMS = ((CraftEntity)target).getHandle();
        Player bukkit = ((EntityPlayer) human).getBukkitEntity();

        if (human.isCrouching()) {
            auto.setTarget(null);
            auto.setAiming(false);
            bukkit.sendMessage(ChatColor.GRAY+"Stopped targeting.");
            return;
        }

        boolean success = auto.setTarget(targetNMS);
        if (!success) {
            bukkit.sendMessage(ChatColor.GRAY+"Cannot aim at self!");
        }
        else
            bukkit.sendMessage(ChatColor.GRAY+"Aiming at target.");

        auto.startAiming();
    }




    private void openMenu(EntityHuman human) {


        if (human.getVehicle() != null) {
            return;
        }

        if (human.isCrouching()) {
            human.getBukkitEntity().openInventory(body.getInventory());
        }

    }
}
