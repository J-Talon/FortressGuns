package me.camm.productions.fortressguns.Util.damage.source;

import me.camm.productions.fortressguns.Util.Adapter;
import me.camm.productions.fortressguns.Util.damage.builder.DamageField;
import me.camm.productions.fortressguns.Util.damage.builder.DamageInfo;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSourceIndirect;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class DamageProvider1_17_1 extends Adapter<Entity, DamageInfo> {


    private static class FGSource1_17_1 extends EntityDamageSourceIndirect {

        private final net.minecraft.world.entity.Entity owner;
        private final net.minecraft.world.entity.Entity projectile;
        private final DamageField field;

        public FGSource1_17_1(String damageId, @Nullable net.minecraft.world.entity.Entity shooter,
                              net.minecraft.world.entity.Entity projectile, DamageField field) {
            super(damageId, shooter, projectile);

            this.owner = shooter;
            this.projectile = projectile;
            this.field = field;
        }

        @Override
        public boolean E() {
            return false;
        }

        @Override
        public net.minecraft.world.entity.Entity getEntity() {
            return projectile;
        }


        @Override
        public IChatBaseComponent getLocalizedDeathMessage(EntityLiving victim) {
            String body = field.getMessage();
            ChatMessage message = new ChatMessage("");

            IChatBaseComponent middle = IChatBaseComponent.a(" " +body+" "); //create a new chat component
            IChatBaseComponent victimName = victim.getScoreboardDisplayName();

            message.addSibling(victimName).addSibling(middle);
            if (owner != null) {
                IChatBaseComponent by= IChatBaseComponent.a("by ");
                IChatBaseComponent killerName = owner.getScoreboardDisplayName();
                message.addSibling(killerName);
            }

            return message;
        }


    }


    @Override
    public void provide(Entity damagee, DamageInfo context) {
        net.minecraft.world.entity.Entity damageeNMS = ((CraftEntity)damagee).getHandle();
        DamageField field = context.getField();

        net.minecraft.world.entity.Entity nmsShooter = context.getShooter() == null ?
                null : ((CraftEntity)context.getShooter()).getHandle();

        net.minecraft.world.entity.Entity nmsProjectile = ((CraftEntity)context.getSource()).getHandle();


        DamageSource source = new FGSource1_17_1(field.getField(), nmsShooter, nmsProjectile, field);
        damageeNMS.damageEntity(source, context.getDamage());
    }




    @Override
    public Class<?> getTarget() {
        return net.minecraft.world.entity.Entity.class;
    }
}
