package org.phlyer.smptools.Waypoints;

import java.io.Serializable;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class Waypoint implements Serializable {
    private final String name;
    private final int[] coordinates;
    private String dimension;

    public Waypoint(String name, int[] coordinates, World.Environment environment){
        this.coordinates = coordinates;
        this.name = name;
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

    public String getName(){
        return name;
    }

    public String toString(){
        return name + " " + coordinates.toString() + " " + dimension;
    }
}
