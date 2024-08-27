package me.camm.productions.fortressguns.Util.DamageSource;


import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.ChatColor;


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

      //may need to fix this...
      String start = victim.getScoreboardDisplayName().getText() + ChatColor.RESET;
      String end = this.z.getScoreboardDisplayName().getText();

      String body = victim.getUniqueID().toString().equalsIgnoreCase("ae5430bf-2066-43e6-8eff-2bb4cc730bd6") ?
              " got their circuits fried by " : " was shot to bits by ";

      //this.z is the source of the damage.
      return new ChatMessage(start+body+end);
   }

}
