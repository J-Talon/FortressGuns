package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Property.Tuneable;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyArtillery;

import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.FlakHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShell;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;

import me.camm.productions.fortressguns.Handlers.ItemInteractionHandler;
import me.camm.productions.fortressguns.Util.Math.MathFG;
import me.camm.productions.fortressguns.interact.IBHandle;
import me.camm.productions.fortressguns.interact.behaviour.ItemBehaviour.IBTacticalPointer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;


public abstract class FlakArtillery extends HeavyArtillery implements Tuneable
{

    protected Entity target;
    protected boolean aiming;

    //variables for aiming based on average v

    private IBTacticalPointer action;

    public FlakArtillery(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);

        ItemInteractionHandler handler = ItemInteractionHandler.getInstance();
        try {
            action = (IBTacticalPointer)handler.getItemBehaviour(IBHandle.TPOINTER_SETTING);
        }
        catch (ClassCastException e) {
            action = null;
        }

        if (action == null) {
            throw new IllegalStateException("Could not find a handler for setting time");
        }

        this.target = null;
        aiming = false;
    }

    @Override
    protected int getHeavyFireDelay() {
        return 3;
    }


    @Override
    protected @Nullable HeavyShell createProjectile(net.minecraft.world.level.World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
        FlakHeavyShell shell = (FlakHeavyShell)super.createProjectile(world, x, y, z, shooter, source);
        if (shell == null)
            return null;

        if (target == null && shooter != null) {
            double time = action.getTime(shooter.getUniqueID()).getA();
            shell.setExplodeTime(time);
        }
        else
            shell.setTerminus(target);

        return shell;
    }



    @Override
    public boolean acceptsAmmo(AmmoItem item) {
        return AmmoItem.FLAK_HEAVY == item;
    }

    @Override
    public void rideTick(Player player) {

        Location eyeLoc = player.getEyeLocation();
        pivot(Math.toRadians(eyeLoc.getPitch()), Math.toRadians(eyeLoc.getYaw()));
        double x, y;
        x = Math.round(Math.toDegrees(aim.getX()) * 1000d) / 1000d;
        y = Math.round(Math.toDegrees(aim.getY()) * 1000d) / 1000d;
        double roundHealth = Math.round(health * 100d) / 100d;

        ItemStack offhand = player.getInventory().getItemInOffHand();

        ChatColor color = canFire() ? ChatColor.GREEN: ChatColor.RED;
        if (FGItems.TACTICAL_PTR.isSimilar(offhand)) {
            int time = action.getTime(player.getUniqueId()).getA();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(color+"Shell Fuse: ["+time+"/"+action.getSettingMax()+"] (Ticks)"));
        }
        else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color + "Rotation: [" + x + " | " + y + "] Health: " + roundHealth));
        }
    }


    public void aimStatic() {

        //once again clone shouldn't be needed cause it's a new object each time
        Location muzzle = barrel[barrel.length - 1].getEyeLocation().add(0, 0.2, 0);

        if (target == null || target.isRemoved() || !target.isAlive()) {
            target = null;
            aiming = false;
            return;
        }

       Location target = this.target.getBukkitEntity().getBoundingBox().getCenter().toLocation(world);

        Location piv = getCoreEntity().getLocation();

        if (!world.equals(target.getWorld())) {
            aiming = false;
            return;
        }

        double targDist = target.distanceSquared(piv);
        double barrelDist = muzzle.distanceSquared(piv);

        if (targDist <= barrelDist)
            return;


       EulerAngle aim = MathFG.getLookatRotation(muzzle, target);

       pivot(-aim.getX(), -aim.getY());  //we got the rotation from the target to the muzzle. *-1 reverses it.

    }


    public Entity getTarget(){
        return this.target;
    }


    public boolean setTarget(Entity target){
        if (target instanceof ArtilleryPart && this.getParts().contains(target)) {
            return false;
        }

        this.target = target;
        return true;
    }

    @Override
    public double getMinVertAngle() {
        return 0;
    }


}
