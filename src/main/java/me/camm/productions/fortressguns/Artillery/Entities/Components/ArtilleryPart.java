package me.camm.productions.fortressguns.Artillery.Entities.Components;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.World;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ArtilleryPart extends ComponentAS
{
    protected Artillery body;
    public ArtilleryPart(World world, Artillery body, double d0, double d1, double d2) {
        super(world, d0, d1, d2, body);
        this.body = body;
    }

    public ArtilleryPart(World world, Artillery body, Location loc){
        this(world, body, loc.getX(),loc.getY(),loc.getZ());
    }


    @Override
    public @NotNull Artillery getBody() {
        return body;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.Q;
    }

    public Sound getSoundHurt(){
        return Sound.BLOCK_BELL_USE;
    }

    public Location getLocation(org.bukkit.World world){
        return new Location(world,u,v,w);
    }


    public Location getEyeLocation(){
        return this.toBukkit().getEyeLocation();
    }

    protected ArmorStand toBukkit(){
        return (ArmorStand)this.getBukkitEntity();
    }



    @Nullable
    protected SoundEffect getSoundDeath() {
        return SoundEffects.gJ;
    }




    @Override
    public boolean onLeftClick(ItemStack mainHand, Player clicked) {

        if (body instanceof Rideable ride) {
            Player rider = ride.getRider();

            if (FGItems.TACTICAL_PTR.isSimilar(mainHand)) {
                if (rider == null)  //here
                    body.fire(clicked);
                else
                    clicked.sendMessage(ChatColor.RED+"Cannot fire; the platform is being commandeered");

                return false;
            }

            if (rider != null && rider.getUniqueId().equals(clicked.getUniqueId())) {
                body.fire(clicked);
                return false;
            }
        }

        return true;
    }



    private void handleRideableRC(Rideable ride, ItemStack mainHand, Player clicked) {
        Player rider = ride.getRider();
        if (rider == null) {

            if (ride.isSeatLocked()) {
                clicked.sendMessage(ChatColor.RED+"Cannot operate, Seat is locked!");
                return;
            }

            ride.getSeat().startRiding(clicked);
        }
        else {
            //the player is the operator
            if (rider.getUniqueId().equals(clicked.getUniqueId())) {
                Material mat = mainHand.getType();

                if (mat == Material.AIR) {
                    body.fire(clicked);
                    return;
                }

                ConstructUtils.openMenu(body, clicked, mainHand);
                return;
            }

            clicked.sendMessage(ChatColor.RED+"Cannot operate, Someone else is using this!");
        }



    }

    @Override
    public void onRightClick(ItemStack mainHand, Player clicked) {

        if (clicked.isSneaking() && clicked.getVehicle() == null) {
            ConstructUtils.openMenu(body, clicked, mainHand);
            return;
        }

        if (body instanceof Rideable ride) {
            this.handleRideableRC(ride, mainHand, clicked);
        }

    }
}
