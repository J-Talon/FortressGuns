package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.StandHelper;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

public class TestGun extends Artillery {


    public TestGun(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);
        barrel = new ArtilleryPart[2];
        base = new ArtilleryPart[0][0];
    }

    @Override
    protected void initInventories() {

    }

    @Override
    public int getMaxAmmo() {
        return 0;
    }

    @Override
    public double getVectorPower() {
        return 0;
    }

    @Override
    public void fire(@Nullable Player shooter) {

    }

    @Override
    public boolean canFire() {
        return false;
    }

    @Override
    public double getMaxHealth() {
        return 1;
    }

    @Override
    public boolean acceptsAmmo(AmmoItem item) {
        return false;
    }

    @Override
    protected boolean instantiateParts() {
        this.pivot = StandHelper.createCore(getCurrentLocation(), new ItemStack(Material.RED_TERRACOTTA),aim,world,this);
        return true;
    }

    @Override
    protected boolean spawnBaseParts() {
        return true;
    }

    @Override
    protected boolean spawnTurretParts() {
        return true;
    }

    @Override
    public ConstructType getType() {
        return ConstructType.DEBUG;
    }
}
