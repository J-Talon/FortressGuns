package me.camm.productions.fortressguns.Artillery.Entities.Components;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Property.AutoTracking;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.ArtilleryItems.ConstructItemHelper;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryCategory;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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


    public Artillery getBody() {
        return body;
    }




    @Override
    public boolean damageEntity(DamageSource source, float damage)
    {
        if (body.isInvalid())
            return super.damageEntity(source,damage);
        else
        {
            Entity entity = source.getEntity();
            if (!(entity instanceof EntityPlayer)) {
                return damageRaw(source, damage);
            }

            EntityHuman human = ((EntityHuman)entity);
            List<org.bukkit.entity.Entity> riders = new ArrayList<>();


            if (body instanceof Rideable rideable) {
                List<Entity> nmsRiders = rideable.getSeat().getPassengers();
                for (Entity nms: nmsRiders) {
                    riders.add(nms.getBukkitEntity());
                }
            }
            else riders = body.getCoreEntity().getPassengers();


            if (!riders.isEmpty()) {
                org.bukkit.entity.Entity e = riders.get(0);
                if (human.equals(e))
                    return false;
            }


            ItemStack holding = human.getItemInMainHand();

            org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asBukkitCopy(holding);
            org.bukkit.inventory.ItemStack pointer = ConstructItemHelper.getStick();

                //if they punch the thing with a stick, fire the cannon instead.


                if (!(pointer.isSimilar(bukkitStack))) {
                    return damageRaw(source, damage);
                }

            if (source instanceof EntityDamageSource && source.u().equals("player")) {
                body.fire(new CraftPlayer(getWorld().getCraftServer(), (EntityPlayer)human));
                return false;
            }
            else return damageRaw(source, damage);





        }

    }

    private boolean damageRaw(DamageSource source, float damage){
        body.playSound(this);
        return body.damage(source, damage);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.Q;
    }

    public Sound getSoundHurt(){
        return Sound.BLOCK_BELL_USE;
    }

    public void setLocation(double x, double y, double z){
        g(x,y,z);
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

    public void seat(EntityHuman human) {

        if (!(body instanceof Rideable rideable)) {
            return;
        }

        ComponentAS seat = rideable.getSeat();
        Player bukkit = (CraftPlayer)human.getBukkitEntity();

        if (seat == null) {
            bukkit.sendMessage(ChatColor.RED+"[!] No valid seat found!");
            return;
        }

        if (seat.getPassengers().size() > 0) {
            bukkit.sendMessage(ChatColor.RED+"[!] Someone else is using this!");
            return;
        }

        if (body instanceof AutoTracking tracking) {
            if (tracking.isAiming()) {
              bukkit.sendMessage(ChatColor.RED+"[!] You cannot use this while it's auto-tracking!");
                return;
            }
        }

        bukkit.sendMessage("Operating "+ ChatColor.RESET+body.getType().getName()+"");
        bukkit.playSound(bukkit.getLocation(),Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.BLOCKS,1,1);


        ((Rideable)body).onMount();
        ((Rideable) body).updateOnInteraction();
        human.startRiding(seat);

        new BukkitRunnable(){

            public void run()
            {
                if (body.isInvalid()) {
                    ((Rideable) body).onDismount();
                    human.stopRiding();
                    cancel();
                }

                Entity vehicle = human.getVehicle();
                if (vehicle != null && vehicle.equals(seat)) {
                    body.rideTick(human);
                }
                else {
                    ((Rideable) body).onDismount();
                    cancel();
                }
            }
        }.runTaskTimer(FortressGuns.getInstance(),0,1);


    }



    /*

    Override of method for interaction
     */
    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand)
    {

        /*
                        a = success
                        b = consume
                        c = consume partial
                        d = pass
                        e = fail
        */

        ItemStack itemstack = entityhuman.b(enumhand);
        if (!this.isMarker() && !itemstack.a(Items.rQ)) {
            if (entityhuman.isSpectator()) {
                return EnumInteractionResult.a;
            } else if (entityhuman.t.y) {
                return EnumInteractionResult.b;
            } else {

                handleInteraction(entityhuman, itemstack);
                if (!(body instanceof Rideable)) {
                    return EnumInteractionResult.b;
                }

                ComponentAS seat = ((Rideable) body).getSeat();

                List<Entity> pass = seat.getPassengers();
                if (pass.isEmpty())
                    return EnumInteractionResult.b;

                if (pass.get(0).equals(entityhuman))
                    return EnumInteractionResult.d;

            }
        } else {
            return EnumInteractionResult.d;
        }
        return EnumInteractionResult.b;
    }



    protected void handleInteraction(EntityHuman human, ItemStack stack) {

        Artillery arty = getBody();


        //basically if they are riding then don't try to seat them again
        if (arty instanceof Rideable ride) {
            ComponentAS seat = ride.getSeat();
            List<Entity> riders = seat.getPassengers();

            if (!riders.isEmpty() && riders.get(0).equals(human)) {

                if (arty.canFire()) {
                    arty.fire((Player)human.getBukkitEntity());
                    return;
                }
            }
        }


        org.bukkit.inventory.ItemStack stackBukkit = CraftItemStack.asBukkitCopy(stack);
        org.bukkit.inventory.ItemStack pointer = ConstructItemHelper.getStick();

        if ((!pointer.isSimilar(stackBukkit))) {

            if (human.isCrouching())
                openMenu(human);
            else
                seat(human);
        }

    }


    protected void openMenu(EntityHuman human) {

        if (human.getVehicle() != null) {
            return;
        }

        if (!human.isCrouching())
            return;

        Player player = (Player)human.getBukkitEntity();
        ConstructInventory menu;
        org.bukkit.inventory.ItemStack stack = player.getInventory().getItemInMainHand();

        FIND_INV:
        {
            if (body instanceof RapidFire rapid && rapid.isJammed()) {
                menu = body.getInventoryGroup().getInventoryByCategory(InventoryCategory.JAM_CLEAR);
                break FIND_INV;
            }

            if (ConstructItemHelper.isAmmoItem(stack) != null) {

                if (!body.requiresReloading()) {
                    return;
                }

                menu = body.getInventoryGroup().getInventoryByCategory(InventoryCategory.RELOADING);
            } else {
                menu = body.getInventoryGroup().getInventoryByCategory(InventoryCategory.MENU);
            }
        }


        if (menu == null) {
            FortressGuns.getInstance().getLogger().warning("Inventory instance returned null!");
            return;
        }

        body.getInventoryGroup().openInventory(menu, player);

        //the idea here is that if player is holding a reloading item, then it will
        //open the reloading inventory, else it will open a menu inventory

    }

}
