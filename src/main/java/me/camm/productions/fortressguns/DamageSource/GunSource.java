package me.camm.productions.fortressguns.DamageSource;


import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;


public class GunSource extends EntityDamageSource {


   private GunSource(String var0, Entity var1) {
      super(var0, var1);
   }

//Must have a boolean A for it to be valid in entityEnderDragon to damage
   public static EntityDamageSource gunShot(EntityHuman entity)
   {
     return new GunSource("gunshot",entity).D();
   }

}
