package me.camm.productions.fortressguns.Util;

import me.camm.productions.fortressguns.DamageSource.GunSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;


public class DamageHelper
{
    public static void damageByGun(Entity damaged, EntityHuman damager, float damage){

        if (damaged instanceof EntityEnderDragon) {
            EntityEnderDragon dragon = (EntityEnderDragon) damaged;
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager.getBukkitEntity(), damaged.getBukkitEntity(), EntityDamageEvent.DamageCause.CUSTOM,damage);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled())
                return;

            damageDragonByGun(dragon,damager,(float)event.getFinalDamage());
        }
        else
        {
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager.getBukkitEntity(),damaged.getBukkitEntity(),EntityDamageEvent.DamageCause.CUSTOM,damage);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled())
                return;

            damaged.damageEntity(GunSource.gunShot(damager),(float)event.getFinalDamage());
        }

    }

    public static void damageByGun(org.bukkit.entity.Entity damaged, org.bukkit.entity.Entity damager, float damage) {
        Entity nmsDamaged = ((CraftEntity) damaged).getHandle();
        Entity nmsDamager =( (CraftEntity)damager).getHandle();

        if (damager instanceof Player) {
            damageByGun(nmsDamaged, (EntityHuman)nmsDamager, damage);
        }
    }

    public static void damageDragonByGun(EntityEnderDragon dragon, Entity damager, float damage)
    {
        if (damager instanceof EntityHuman)
        dragon.damageEntity(GunSource.gunShot((EntityHuman)damager),damage);
    }
}
