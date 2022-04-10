package me.camm.productions.fortressguns.Artillery;

import me.camm.productions.fortressguns.Util.StandHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class LightArtillery extends FieldArtillery
{
    private static final double POWER;
    private static final int TIME;
    private static final double RECOVER_RATE;
    private static final double HEALTH;

    static {
        POWER = 4;
        TIME = 1;
        RECOVER_RATE = 0.15;
        HEALTH = 20;
    }


    public LightArtillery(Location loc, World world) {
        super(loc, world);
        barrel = new ArtilleryPart[5];
        base = new ArtilleryPart[3][3];
    }

    protected static ItemStack BODY = new ItemStack(Material.GREEN_TERRACOTTA);
    protected static ItemStack BASE_CLOSE = new ItemStack(Material.YELLOW_TERRACOTTA);
    protected static ItemStack BASE_FAR = new ItemStack(Material.STONE_BRICK_SLAB);
    protected static ItemStack BARREL_MAT = new ItemStack(Material.DISPENSER);

    @Override
    public void fire() {
       super.fire(POWER,TIME,RECOVER_RATE);
    }



    @Override
    public ArtilleryType getType() {
        return ArtilleryType.FIELD_LIGHT;
    }

    @Override
    public double getMaxHealth() {
        return HEALTH;
    }

    @Override
    public void spawn()
    {

        pivot = StandHelper.getCore(loc, BODY, aim, world, this);
        for (int slot=0;slot< barrel.length;slot++)
        {
            boolean small = false;
            double totalDistance;

            if (slot>=1) {
                totalDistance = (LARGE_BLOCK_LENGTH * 0.75 + 0.5 * SMALL_BLOCK_LENGTH) + (slot * SMALL_BLOCK_LENGTH);
                small = true;
            }
            else
                totalDistance = (slot+1)* LARGE_BLOCK_LENGTH;


            double height = -totalDistance*Math.sin(aim.getX());
            double horizontalDistance = totalDistance*Math.cos(aim.getX());

            double z = horizontalDistance*Math.cos(aim.getY());
            double x = -horizontalDistance*Math.sin(aim.getY());

            Location centre = pivot.getLocation(world).clone();
            ArtilleryPart stand;

            //if it is small, add 0.75 so that it is high enough
            if (small) {
                stand = StandHelper.spawnPart(centre.add(x, height + 0.75, z), BARREL_MAT, aim, world, this);
                stand.setSmall(true);
            }
            else
                stand = StandHelper.spawnPart(centre.add(x, height, z),BODY,aim,world,this);

            barrel[slot] = stand;
        }


        double rads = -Math.PI/3;
        int bar = 0;


        //for the base of the artillery
        for (ArtilleryPart[] standRow: base) {
            double[] position = getBasePositions(rads);  //get the x, z values for the base

            int length = 0;

            for (int slot=0;slot<standRow.length;slot++) {
                Location loc = pivot.getLocation(this.world).clone().add((position[0]*LARGE_BLOCK_LENGTH+length*position[0]),
                        -0.75, (LARGE_BLOCK_LENGTH*position[1]+length*position[1]));

                //(World world, Artillery body, double d0, double d1, double d2)
                ArtilleryPart part;

                //if the length is close to base, then give it wheels, else give it
                //supports
                if (length >=1)
                    part = StandHelper.spawnPart(loc,BASE_FAR,aim,world,this);
                else if (bar!=base.length-1)
                    part = StandHelper.spawnPart(loc,BASE_CLOSE,aim,world, this);
                else
                    part = StandHelper.spawnPart(loc,BODY,aim,world, this);

                length ++;
                standRow[slot] = part;
            }
            bar ++;

            if (bar == base.length-1)
                rads = Math.PI; //so that it is directly behind the cannon (facing south)
            else
                rads += 2 * Math.PI / 3;
        }
        initLoadedChunks();
        setHealth(HEALTH);
        pivot(0,0);

    }


}
