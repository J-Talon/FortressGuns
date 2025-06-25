package me.camm.productions.fortressguns.Artillery.Projectiles.Missile;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import static me.camm.productions.fortressguns.Util.MathLib.getOrthogonal;

public class SimpleMissile extends AbstractRocket implements ProjectileFG, ProjectileExplosive {

    private final org.bukkit.World bukkitWorld;
    private int fueledFlightAge;
    private Vec3D direction;

    private static final double ACCELERATION = 0.2;
    private static final double MAX_ACCELERATION = 0.3;
    private static final double MAX_SPEED_SQUARED = 6; //slightly faster than max spd elytra
    private static final double MAX_SPEED;
    private static final double ORBIT_DIST = 17;
    private static final int DIST_EXPLODE_SQUARED = 10;  //slightly more than 3b
    private int readyTime;
    private static final int FUEL = 600;  //
    private static final int PRIME = 5; //1/2 sec

    private final Vec3D initialVelocity;
    private final boolean hadTarget;
    private final double sineOffset;

    private float initialXRot, initialYRot;
    private Vector targetOrthogonal;

    private final MissileLockNotifier notifier;


    private static float explosionPower; //4
    private static final int difficulty;


    private Vec3D terminalVel = null;



    static {
        org.bukkit.inventory.ItemStack bukkitVer = new org.bukkit.inventory.ItemStack(Material.LEVER);
        ItemMeta meta = bukkitVer.getItemMeta();
        meta.setDisplayName("Rocket");
        bukkitVer.setItemMeta(meta);
        MAX_SPEED = Math.sqrt(MAX_SPEED_SQUARED);
        explosionPower = 4;
        difficulty = 5;
    }

    public SimpleMissile(World world, double x, double y, double z, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, x, y, z, shooter, source);
        fueledFlightAge = 0;
        direction = null;
        readyTime = 0;
        Vector initial = Construct.eulerToVec(source.getAim());
        initialVelocity = new Vec3D(initial.getX(),initial.getY(),initial.getZ());
        hadTarget = false;
        bukkitWorld = world.getWorld();

        sineOffset = rand.nextDouble() * Math.PI * 2; // 2PI = period of sine function
        initialYRot = initialXRot = Float.NaN;
        notifier = MissileLockNotifier.get(FortressGuns.getInstance());
        //velocity is in blocks/tick
    }

    public static void setExplosionPower(float explosionPower) {
        SimpleMissile.explosionPower = explosionPower;
    }


    @Override
    public void inactiveTick() {
        explode(null);
        super.inactiveTick();
    }
    @Override
    public float getExplosionPower() {
        return explosionPower;
    }


    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        explode(getPositionVector());
        return true;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        Block hit = bukkitWorld.getBlockAt(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ());
        if (hit.getType().isAir())
            return false;

        explode(exactHitPosition);
        return true;
    }


    @Override
    public float getHitDamage() {
        return 0;
    }


    public static int getFuelTicks(){
        return FUEL;
    }

    @Override
    public void explode(@Nullable Vec3D hit) {

        if (target != null && target instanceof Player) {
            notifier.exitNotification(target.getUniqueId());
        }
        Location explosionLoc;
        org.bukkit.World world = getWorld().getWorld();

        //this.getWorld().createExplosion(this, locX(), locY(), locZ(), getDamageStrength(), false, Explosion.Effect.c);
        //change this to use our custom explosion stuff later

        ExplosionFactory.missileExplosion(world, shooter.getBukkitEntity(),locX(), locY(),locZ(),4);

       // world.spawnParticle(Particle.EXPLOSION_HUGE,explosionLoc,1,0,0,0,0,null, true);

        this.die();
    }




    private void playEffects(Location loc) {

        Vec3D motion = getMot();
        motion = motion.e();  ///e() --> multiply(-1)

        //0.00015x^{2}

        //maybe this should be an option
        // I know some peeps want the small explode particles instead
//        final ParticleParam PARAM = Particles.v;  //end rod particle
//        bukkitWorld.getPlayers().forEach(player -> {
//
//            double distSquared = loc.distanceSquared(player.getLocation());
//            double glow = Math.max((-0.0008 * distSquared) + 5, 1);
//
//            PacketPlayOutWorldParticles packet;
//            EntityPlayer nms = ((CraftPlayer)player).getHandle();
//
//            for (double angle = 0; angle < 2*Math.PI; angle += Math.PI / 2) {
//                Location next = loc.clone();
//                double z = Math.cos(angle);
//                double x = -Math.sin(angle);
//
//                for (int num = 0; num < glow; num ++) {
//                    packet = new PacketPlayOutWorldParticles(PARAM, true, next.getX(),next.getY(), next.getZ(), (float)9.9E5, 0,0,1,0);
//                    nms.b.sendPacket(packet);
//                    next.add(x * 0.1 * num,0,z * 0.1 * num); /// 0.1 is arbitrary dist
//                }
//            }
//
//            for (int num = 0; num < glow; num ++) {
//                Location next = loc.clone();
//
//                next.add(0,0.1 * num,0);
//                packet = new PacketPlayOutWorldParticles(PARAM, true, next.getX(),next.getY(), next.getZ(), (float)9.9E5, 0,0,1,0);
//                nms.b.sendPacket(packet);
//
//                next.add(0,-0.2 * num,0);
//                packet = new PacketPlayOutWorldParticles(PARAM, true, next.getX(),next.getY(), next.getZ(), (float)9.9E5, 0,0,1,0);
//                nms.b.sendPacket(packet);
//            }
//
//        });


        //some people like this more???
        double x = motion.getX(), y = motion.getY(), z = motion.getZ();

        bukkitWorld.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,loc,0,x, y, z,0.2,null,true);
        bukkitWorld.spawnParticle(Particle.FLAME,loc,0,x, y, z,0.2,null, true);
        bukkitWorld.playSound(loc, Sound.ITEM_ARMOR_EQUIP_LEATHER,SoundCategory.BLOCKS,3,0.1f);
    }


    @Override
    public void tick() {
        super.tick();

        if (readyTime < PRIME) {
            readyTime++;

            if (readyTime >= PRIME) {
                setNoGravity(true);
            }
            return;
        }

        Location loc = new Location(bukkitWorld, locX(), locY(), locZ());
        bukkitWorld.spawnParticle(Particle.EXPLOSION_LARGE,loc,0,0,0,0,1,null, true);

        if (fueledFlightAge >= FUEL) {
            this.setNoGravity(false);

            if (target != null) {
                notifier.exitNotification(target.getUniqueId());
            }

            return;
        }

        fueledFlightAge++;
        playEffects(loc);

        if (target == null) {
            if (direction == null) {

                if (hadTarget)
                    direction = getMot().d();
                else
                    direction = initialVelocity;
            }

            flyNormally();
        }
        else {
            flyToTarget(loc);
            direction = null;
        }


    }


    private void flyNormally() {
        Vec3D motion = getMot();
        double magnitudeSquared = motion.g(); // length squared
        if (magnitudeSquared >= MAX_SPEED_SQUARED) {  //if >= than max speed
            //a = multiply()

            if (terminalVel == null) {
                terminalVel = motion.d().a(MAX_SPEED);
            }
            else setMot(terminalVel);

        }
        else {

            //there isn't really a way nextMagnitude == 0 assuming the rocket isn't modified midflight
            //but just in case cause we don't want it zero-ing out
            //cause I know that they're susceptible to commandblock modification
            double nextMagnitude = magnitudeSquared + (ACCELERATION * ACCELERATION);
            if (nextMagnitude == 0) {
                double dirX = direction.getX() * ACCELERATION,
                        dirY = direction.getY() * ACCELERATION,
                        dirZ = direction.getZ() * ACCELERATION;

                motion = motion.add(dirX, dirY, dirZ);


            } else {
                double mult = Math.sqrt(magnitudeSquared / nextMagnitude);
                motion = motion.a(mult + 1);
            }

            setMot(motion);
        }

        //motion changed
        this.C = true;


    }


    private void flyToTarget(Location missileLoc) {

        if (target instanceof Player && (!(((Player) target).isGliding()))) {
            notifier.removeNotification(target.getUniqueId());
        }

        if (target.isDead() || !target.isValid() || (target.getWorld() != bukkitWorld)) {

            notifier.removeNotification(target.getUniqueId());
            target = null;
            return;
        }

        Location targetLoc = target.getLocation();

        if (targetLoc.distanceSquared(missileLoc) <= DIST_EXPLODE_SQUARED) {
            explode(null);
            return;
        }

        float yaw = targetLoc.getYaw();
        float pitch = targetLoc.getPitch();

        EulerAngle targetFacing = new EulerAngle(pitch, yaw, 0);
        Vector targetFacingVec = Construct.eulerToVec(targetFacing);

        //turbulence calculations
        //arbitrary values from artistic standpoint
        double targetSpeed = target.getVelocity().length();  //sqrt here
        double turbulence = Math.pow(Math.E, -17 * (targetSpeed - 1.6) * (targetSpeed - 1.6));
        double acceleration = 0.1 * (targetSpeed - 0.2) * (targetSpeed - 0.85);




        ////orthogonal calculations
        ///0.03 is period. arbitrary artistic decision
        double offset = Math.sin(0.03 * fueledFlightAge + sineOffset) * ORBIT_DIST;

        Vector targetLocVec = targetLoc.toVector();

        if (Float.isNaN(initialXRot) && Float.isNaN(initialYRot)) {
            initialYRot = yaw;
            initialXRot = pitch;
        }



        Vector trackingLocation;

        if (offset == 0) {
            targetOrthogonal = null;
           trackingLocation = targetLocVec;
        }
        else {
            if (targetOrthogonal == null)
                targetOrthogonal = getOrthogonal(targetFacingVec);
            double diffYaw = Math.toRadians(yaw - initialYRot);
            double diffPitch = Math.toRadians(pitch - initialXRot);

            Vector nextOrtho = targetOrthogonal.clone().rotateAroundY(diffYaw).rotateAroundX(diffPitch); //
            trackingLocation = targetLocVec.clone().add(nextOrtho.multiply(offset * turbulence));
        }

        Vector travelTowards = trackingLocation.subtract(missileLoc.toVector());
        travelTowards.normalize();   //sqrt here

        //determining where to travel towards
        travelTowards.multiply(Math.min(ACCELERATION + acceleration, MAX_ACCELERATION));  //arbitrary

        Vec3D motion = getMot();


        final double OFFSET_X = 1.5, OFFSET_Y = 1, PEN_FACTOR = 0.3, ALIGN = OFFSET_Y - PEN_FACTOR * OFFSET_X;

        double originalMotionPercent = -Math.abs(0.15 * (targetSpeed-OFFSET_X)) + 0.8;

        //the less the target is moving, the more accurate the missiles are
        //this is to help prevent the "spiralling behaviour" when targets don't move as much
        //and then the missile just ends up circling them
        double idlePenalty = Math.min(PEN_FACTOR * targetSpeed + ALIGN, OFFSET_Y);

        originalMotionPercent = Math.max(0, originalMotionPercent + idlePenalty - OFFSET_Y);

        /*
        x = target speed
        w           (offset y)  changing w shouldn't affect anything. this is just to clamp the thing at 1
        p           (offset x)
        a           (idle penalty factor  << change this for the difficulty addition before speed = 1.5)
        b = w - ap  (aligns the point for where y = w for the penalty function to the motion function)

        g = -abs(0.15(x+p)) + 0.8  (motion percent function)
        h = min(ax + b, w)         (penalty function)

        y = max(g + h - w, 0)      (final function for the missile difficulty)
                                    numbers closer to 0 mean that the missiles are more accurate

         */




        double accAmount = (1 - originalMotionPercent) + 1;
        travelTowards.multiply(accAmount);

        motion.a(originalMotionPercent);  //vector.multiply() in NMS
        motion = motion.add(travelTowards.getX(), travelTowards.getY(), travelTowards.getZ());

        if (motion.g() > MAX_SPEED_SQUARED) {   //vector.magnitudeSquared()
            motion = motion.d().a(MAX_SPEED);  //vector.magnitude().multiply()
        }


        setMot(motion);
        this.C = true;
    }



    @Override
    public float getWeight() {
        return 0.9F;
    }

}
