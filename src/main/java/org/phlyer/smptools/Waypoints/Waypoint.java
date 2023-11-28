package org.phlyer.smptools.Waypoints;

import java.io.Serializable;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class Waypoint implements Serializable {
    private int[] coordinates = new int[3];
    private String dimension;

    public Waypoint(int[] coordinates, World.Environment environment){
        this.coordinates = coordinates;
        switch (environment){
            case NORMAL: dimension = "Overworld";
            break;
            case NETHER: dimension = "Nether";
            break;
            case THE_END: dimension = "End";
            break;
        }
    }

    public int[] getCoordinates(){
        return coordinates;
    }

    public String getDimension(){
        return dimension;
    }
}
