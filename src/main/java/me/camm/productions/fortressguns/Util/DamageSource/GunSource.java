package me.camm.productions.fortressguns.Util.DamageSource;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.EntityDamageSourceIndirect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;



public class GunSource extends EntityDamageSourceIndirect {

   private final Entity owner;


   private GunSource(String var0, Entity projectile, Entity owner) {
      super(var0, projectile, owner);
      this.owner = owner;
   }

   public static EntityDamageSource gunShot(EntityHuman entity, Entity projectile)
   {
     return new GunSource("gunshot",projectile,entity).D();
   }


   //boolean isThorns()
   @Override
   public boolean E() {
      return false;
   }

   /*
   This is the method that controls the death message for when an entity is killed.
   See: CombatTracker, IChatMutableComponent
    */
   @Override
   public IChatBaseComponent getLocalizedDeathMessage(EntityLiving victim) {


      String body = victim.getUniqueID().toString().equalsIgnoreCase("ae5430bf-2066-43e6-8eff-2bb4cc730bd6") ?
              " was reduced to electronic waste " : " was shot to bits by ";

      ChatMessage message = new ChatMessage("");
      IChatBaseComponent killerName = owner.getScoreboardDisplayName();
      IChatBaseComponent victimName = victim.getScoreboardDisplayName();
      IChatBaseComponent middle = IChatBaseComponent.a(body); //create a new chat component

      message.addSibling(victimName).addSibling(middle).addSibling(killerName);
      return message;
   }

}
