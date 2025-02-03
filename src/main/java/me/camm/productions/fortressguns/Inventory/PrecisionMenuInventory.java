package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemHelper;
import me.camm.productions.fortressguns.Inventory.Abstract.*;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.List;

public class PrecisionMenuInventory extends MenuInventory {

    static MenuFunction aimVertical = new MenuFunction() {
        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {
            ItemStack stack = event.getCurrentItem();

            //preconditions should already check the validity of lore so
            //not sure what intellij is yapping about
            if (!(preconditions(stack)))
                    return;

            List<String> lore = stack.getItemMeta().getLore();
            String degrees = lore.get(0);
            degrees = degrees.split(ChatColor.WHITE+"")[1].split(" ")[0];
            double targetVertical = Math.toRadians(convert(degrees)) * -1;

            Player player = (Player)event.getWhoClicked();
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK,1,1);

            Artillery art = (Artillery) body;
            EulerAngle interpolated = art.getInterpolatedAim();
            interpolated = interpolated.setX(interpolated.getX() + targetVertical);
            art.setInterpolatedAim(interpolated);
            art.startPivotInterpolation();
        }
    };


    static MenuFunction aimHorizontal = new MenuFunction() {
        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {
            ItemStack stack = event.getCurrentItem();

            if (!(preconditions(stack)))
                return;

            Player player = (Player)event.getWhoClicked();
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK,1,1);

            List<String> lore = stack.getItemMeta().getLore();
            String degrees = lore.get(0);
            degrees = degrees.split(ChatColor.WHITE+"")[1].split(" ")[0];
            double targetHorizontal = Math.toRadians(convert(degrees));

            Artillery art = (Artillery) body;
            EulerAngle interpolated = art.getInterpolatedAim();
            interpolated = interpolated.setY(interpolated.getY() + targetHorizontal);
            art.setInterpolatedAim(interpolated);
            art.startPivotInterpolation();
        }
    };


    static MenuFunction fire = new MenuFunction() {
        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {
            Artillery art = (Artillery) body;
            if (art.canFire()) {
                art.fire((Player)event.getWhoClicked());
            }
        }
    };





    public PrecisionMenuInventory(Artillery owner, InventoryGroup group) {
        super(owner, group);
        functions.put(Button.ROTATE_HORIZONTAL.getName(), aimHorizontal);
        functions.put(Button.ROTATE_VERTICAL.getName(), aimVertical);
        functions.put(Button.FIRE.getName(), fire);
        functions.put(Button.DISASSEMBLE.getName(), disassemble);
        functions.put(Button.RELOAD.getName(), openReloading);
        init();
    }

    @Override
    protected boolean isStaticItem(ItemStack current) {
        for (Button b: Button.values()) {
            if (ArtilleryItemHelper.matchesName(current, b.getName()))
                return true;
        }
        return false;
    }


    @Override
    public void init() {
        double[] settings = new double[]{0.1, 1, 5, 10, 30, 45};
        ItemStack border = Button.BORDER.toItemRaw();

        int i;
        for ( i = 0; i < 6; i ++) {
            String value = ChatColor.WHITE +""+ settings[i] +" degrees";
            String nValue = ChatColor.WHITE +"-"+ settings[i] +" degrees";


            gui.setItem(9 * i + 6, border);

            ItemStack label = Button.INFO.toItem(ChatColor.WHITE+"Use the buttons to rotate the turret");
            gui.setItem(9 * i, label);
            gui.setItem(9 * i + 3, label);

            ItemStack upHor = Button.ROTATE_HORIZONTAL.toItem(value);
            gui.setItem(9 * i + 2, upHor);

            ItemStack downHor = Button.ROTATE_HORIZONTAL.toItem(nValue);
            gui.setItem(9 * i + 1, downHor);

            ItemStack upVert = Button.ROTATE_VERTICAL.toItem(value);
            gui.setItem(9 * i + 5, upVert);

            ItemStack downVert = Button.ROTATE_VERTICAL.toItem(nValue);
            gui.setItem(9 * i + 4, downVert);
        }

        ItemStack fire = Button.FIRE.toItemRaw();
        ItemStack reload = Button.RELOAD.toItemRaw();
        ItemStack disassemble = Button.DISASSEMBLE.toItemRaw();

        for (i = 0; i < 3; i++ ) {
            gui.setItem(9 * i + 8, fire);
            gui.setItem(9 * i + 7, fire);
        }

        for (i = 3; i < 5; i++) {
            gui.setItem(9 * i + 8, reload);
            gui.setItem(9 * i + 7, reload);
        }

        gui.setItem(52, disassemble);
        gui.setItem(53, disassemble);

    }


    @Override
    public void updateState() {

        Artillery art = (Artillery)owner;
        EulerAngle aim = art.getAim();
        double x = Math.toDegrees(aim.getX());
        x = Math.round((x * 10)) / 10.0;

        double y = Math.toDegrees(aim.getY());
        y = Math.round((y * 10)) / 10.0;

        String rotation = ChatColor.RED + "["+y +" | "+ x +"]";
        String loaded = ChatColor.BLUE +""+ art.getAmmo();
        rotation = rotation +" "+ loaded;

        ItemStack rotationInfo = Button.INFO.toItem(rotation);
        for (int i = 0; i < 6; i ++) {
            gui.setItem(9 * i + 3, rotationInfo);
        }


    }
}
