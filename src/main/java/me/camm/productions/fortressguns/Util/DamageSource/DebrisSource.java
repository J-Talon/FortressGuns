package me.camm.productions.fortressguns.Util.DamageSource;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.EntityDamageSourceIndirect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;

public class DebrisSource extends EntityDamageSourceIndirect {

    private final Entity owner;


    private DebrisSource(String var0, Entity projectile, Entity owner) {
        super(var0, projectile, owner);
        this.owner = owner;
    }

    public static EntityDamageSource debris(EntityHuman entity, Entity projectile)
    {
        return new DebrisSource("debris",projectile,entity).D();
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


        String body = " was hit by debris in ";
        String end = "'s bombardment";

        ChatMessage message = new ChatMessage("");
        IChatBaseComponent killerName = owner.getScoreboardDisplayName();
        IChatBaseComponent victimName = victim.getScoreboardDisplayName();
        IChatBaseComponent middle = IChatBaseComponent.a(body); //create a new chat component
        IChatBaseComponent finalization = IChatBaseComponent.a(end);

        message.addSibling(victimName).addSibling(middle).addSibling(killerName).addSibling(finalization);
        return message;
    }
}
