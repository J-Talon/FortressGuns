package me.camm.productions.fortressguns.DamageSource;


import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;


public class GunSource extends EntityDamageSource {


   private GunSource(String var0, Entity var1) {
      super(var0, var1);
   }

   public static EntityDamageSource gunShot(EntityHuman entity)
   {
     return new GunSource("gunshot",entity).D();
   }


   /*
   This is the method that controls the death message for when an entity is killed.

    */
   @Override
   public IChatBaseComponent getLocalizedDeathMessage(EntityLiving victim) {

      String body = " was shot to bits by ";

      //new Object[]{victim.getScoreboardDisplayName(), this.z.getScoreboardDisplayName(), killingItem.G()})
      //new ChatMessage(var2, victim.getScoreboardDisplayName(), this.z.getScoreboardDisplayName())
      System.out.println(victim.getName() +" "+
      victim.getScoreboardDisplayName() +" "+
      victim.getCustomName() +" "+
      victim.getDisplayName());


      //this.z is the source of the damage.
      return new ChatMessage(body, victim.getScoreboardDisplayName(),this.z.getScoreboardDisplayName());
   }

}
