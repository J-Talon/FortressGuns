package me.camm.productions.fortressguns.Artillery.Projectiles.Missile;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Projectiles.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class AbstractRocket extends EntityArrow implements ArtilleryProjectile, ProjectileExplosive {

    protected Entity target;
    protected EntityPlayer shooter;
    protected Artillery source;
    protected boolean hadTarget;
    private final MissileLockNotifier notifier;

    protected static final ItemStack item;
    protected Random rand;

    static {
        org.bukkit.inventory.ItemStack bukkitVer = new org.bukkit.inventory.ItemStack(Material.LEVER);
        ItemMeta meta = bukkitVer.getItemMeta();
        meta.setDisplayName("Rocket");
        bukkitVer.setItemMeta(meta);
        item = CraftItemStack.asNMSCopy(bukkitVer);

    }


    public AbstractRocket(World world, double x, double y, double z, @Nullable EntityPlayer shooter, Artillery source) {
        super(EntityTypes.d, x, y, z, world);
        this.shooter = shooter;
        this.source = source;
        notifier = MissileLockNotifier.get(FortressGuns.getInstance());
        hadTarget = false;
        rand = new Random();
    }


    public void setTarget(Entity target) {
        this.target = target;
        if (target instanceof Player) {
            notifier.addNotification(target.getUniqueId());
        }

        hadTarget = true;
    }

    @Override
    public void a(MovingObjectPosition pos) {
        preHit(pos);
    }

    @Override
    protected void a(MovingObjectPositionEntity pos) { preHit(pos);}

    @Override
    protected ItemStack getItemStack() {
        return item;
    }
}
