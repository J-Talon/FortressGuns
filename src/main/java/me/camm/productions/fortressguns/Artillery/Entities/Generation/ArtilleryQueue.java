package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class ArtilleryQueue {

    private byte pos;
    private final Artillery[] artillery;

    public ArtilleryQueue(){
        artillery = new Artillery[3];
        pos = 0;
    }

    public boolean addArtillery(Artillery art) {
        if (art instanceof RapidFire)
            return false;

        pos ++;
        if (pos == artillery.length)
            pos = 0;

        artillery[pos] = art;
        return true;
    }

    public void doAction(Method action, Player caller, @Nullable Object...args) {
        for (int index = 0; index < artillery.length; index ++) {
            Artillery a = artillery[index];
            if (a == null)
                continue;

            if (!a.chunkLoaded()) {
                artillery[index] = null;
                caller.sendMessage("The artillery at "+a.getInitialLocation().toString()+" " +
                        "is unloaded and cannot perform that action.");
                continue;
            }

            if (a instanceof Rideable && ((Rideable) a).hasRider()) {
                artillery[index] = null;
                caller.sendMessage("The artillery at "+a.getInitialLocation().toString()+" " +
                        "has an operator and cannot perform that action.");
                continue;
            }

            try {
                if (args == null) {
                    action.invoke(a);
                }
                else action.invoke(a,args);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
