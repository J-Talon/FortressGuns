package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.FireTrigger;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;

import org.bukkit.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.util.EulerAngle;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;


/*
 * @author CAMM
 */
public abstract class RapidFire extends Artillery implements BulkLoaded {


    protected Vector projectileVelocity;
    protected static ItemStack CASING;


    protected FireTrigger triggerHandle;

    static {
        CASING = new ItemStack(Material.IRON_NUGGET);
        if (CASING.getItemMeta() != null) {
            ItemMeta meta = CASING.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Bullet Casing");
            CASING.setItemMeta(meta);

        }
    }




    public RapidFire(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
        projectileVelocity = new Vector(0,0,0);
    }

    public abstract double getRange();

}
