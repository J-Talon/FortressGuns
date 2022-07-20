package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import net.minecraft.world.entity.Entity;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Set;

public class Spotter implements Construct {

   private final Location loc;
   private Component core;
   private boolean loaded;
   private boolean dead;
   private double health;

   protected static double HEALTH;


   static {
       HEALTH = 10;
   }

   private Entity target;

    private Artillery[] links;

    public Spotter(Location loc){
        this.loc = loc;
        links = new Artillery[3];
    }

    public Spotter(Location loc, Entity target) {
        this.loc = loc;
        this.target = target;
    }


    @Override
    public void spawn() {

        init();
    }

    protected void init(){

    }



    @Override
    public void setChunkLoaded(boolean loaded) {

    }

    @Override
    public Set<Chunk> getLoaders() {
        return null;
    }

    @Override
    public void unload(boolean a, boolean b) {

    }

    @Override
    public boolean inValid() {
        return false;
    }


}
