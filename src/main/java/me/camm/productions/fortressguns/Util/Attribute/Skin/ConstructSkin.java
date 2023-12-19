package me.camm.productions.fortressguns.Util.Attribute.Skin;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class ConstructSkin {
    protected static enum TurretComponent {
        BARREL(Material.DISPENSER),
        WHEEL(Material.COAL_BLOCK),
        BODY(Material.GREEN_TERRACOTTA),
        SEAT(Material.BIRCH_TRAPDOOR),
        BASE(Material.STONE_SLAB);
        private final Material material;

        TurretComponent(Material mat) {
            this.material = mat;
        }

        public ItemStack toItem() {
            return new ItemStack(material);
        }

        public Material getMat() {
            return material;
        }
    }


    static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    }



    protected String compatibility;
    protected Map<TurretComponent, Material> skinMap;

    protected abstract void loadSkinOrDefault();

    protected abstract TurretComponent[] getTurretMappings();

    protected abstract void loadDefault();




    public ConstructSkin() {
        skinMap = new HashMap<>();
        compatibility = null;
    }

    protected void computeCompat() {
        StringBuilder builder = new StringBuilder();
        List<Integer> list = new ArrayList<>();

        for (TurretComponent component: skinMap.keySet())
            list.add(component.ordinal());

        list.sort(new IntComparator());
        for (int current: list)
            builder.append(current);

        compatibility = builder.toString();
    }


    public Map<TurretComponent, Material> getSkinMap() {
        return skinMap;
    }
}




