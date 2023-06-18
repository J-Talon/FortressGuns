package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.EnumPistonReaction;

public class Component extends EntityArmorStand {

    protected Construct body;
    public Component(World world, double d0, double d1, double d2, Construct body) {
        super(world, d0, d1, d2);
        this.body = body;

    }
    public EnumPistonReaction getPushReaction() {
        return EnumPistonReaction.d;
    }



    




}
