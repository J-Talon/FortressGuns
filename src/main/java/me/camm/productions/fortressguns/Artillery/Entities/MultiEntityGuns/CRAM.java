package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties.AutoTracking;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.CRAMShell;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Artillery.Entities.StandHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CRAM extends Artillery implements AutoTracking {
    private boolean aiming;
    private static double maxHealth;
    private Entity target;

    public CRAM(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
        aiming = false;
        this.target = null;

        this.base = new ArtilleryPart[1][1];  //temporary
        this.barrel = new ArtilleryPart[1];
    }

    static {
        maxHealth = 30;
    }

    @Override
    protected void initInventories() {
        interactionInv = new InventoryGroup.RapidGroup(this);
    }

    public List<ArtilleryPart> getParts() {
        List<ArtilleryPart> parts = new ArrayList<>();
        parts.add(pivot);
        return parts;
    }

    public static void setMaxHealth(double max) {
        maxHealth = max;
    }
    @Override
    protected boolean spawnTurretParts() {
        return false;
    }

    @Override
    public int getMaxAmmo() {
        return -1;
    }


    @Override
    protected boolean spawnParts() {
        pivot = StandHelper.createCore(loc,new ItemStack(Material.STONE),aim,world,this);
        return true;
    }

    @Override
    protected boolean spawnBaseParts() {
        return true;
    }


    // oh look a civilian airliner
    @Override
    public void fire(@Nullable Player shooter) {

        if (target == null || target.isRemoved() || !target.isAlive())
            return;
//
//        Vector a;
//        a.rotateAroundAxis();


        /*
        bs = bullet spd
        tv = target velocity
        tp = target pos

        a,b,c are from the quadratic equation

         */

        double bs = 4;


        Vector targetPos = new Vector(target.locX(), target.locY(), target.locZ());
        Vector shooterPos = pivot.getEyeLocation().toVector();



        Vector tv;
        Vec3D mot = target.getMot();
        tv = new Vector(mot.getX(), mot.getY(), mot.getZ());

       // tv.multiply(1/20);

        //target pos relative to shooter
        Vector tp = targetPos.clone().subtract(shooterPos);

        System.out.println("tp: "+tp);
        System.out.println("raw tv: "+mot.toString());
        System.out.println("tv: "+tv);
        System.out.println("sp: "+shooterPos);

        double a = tv.dot(tv) - (bs * bs);
        double b = tp.dot(tv) * 2;
        double c = tp.dot(tp);


        double discriminant = b*b - (4*a*c);
        if (discriminant < 0)
            return;

        double deltaTPos = ((-b) + Math.sqrt(discriminant) ) / (2 * a);
        double deltaTNeg = ((-b) - (Math.sqrt(discriminant))) / (2 * a);

        double deltaTime = -1;

        if (deltaTPos > 0) {
            deltaTime = deltaTPos;
        }
        else if (deltaTNeg > 0) {
            deltaTime = deltaTNeg;
        }

        if (deltaTime < 0)
            return;

      //  deltaTime = deltaTime / 20;


        Vector direction = tp.clone().add(tv.clone().multiply(deltaTime));

        double denominator = deltaTime * bs;

        System.out.println(deltaTime);

        direction.multiply(1/denominator);

        direction.normalize();
        direction.multiply(bs);

        System.out.println("direction: "+direction.toString());

        net.minecraft.world.level.World nms = ((CraftWorld)world).getHandle();
        CRAMShell shell = new CRAMShell(nms,shooterPos.getX(), shooterPos.getY(), shooterPos.getZ(),null,this);

        Vec3D dir = new Vec3D(direction.getX(), direction.getY(), direction.getZ());
        shell.setMot(dir);
        nms.addEntity(shell);

        System.out.println("shooting");


    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.CRAM;
    }

    @Override
    public boolean canFire() {
        return target != null;
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
    }

    @Override
    public double getVectorPower() {
        return 4;
    }

    @Override
    public boolean isAiming() {
        return aiming;
    }

    @Override
    public boolean setTarget(Entity target) {
        if (target instanceof ArtilleryPart && this.getParts().contains(target)) {
            return false;
        }

        this.target = target;
        return true;
    }

    @Override
    public void setAiming(boolean aiming) {
        this.aiming = aiming;
    }


    @Override
    public boolean acceptsAmmo(AmmoItem item) {
        return AmmoItem.CRAM == item;
    }

    @Override
    public void startAiming() {

    }
}
