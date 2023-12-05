package org.phlyer.smptools;

import org.bukkit.plugin.java.JavaPlugin;
import org.phlyer.smptools.AdminCommands.ClearPlayerPDC;
import org.phlyer.smptools.PlayerCommands.CoordinatesBroadcast;
import org.phlyer.smptools.Waypoints.WaypointCommand;
import org.phlyer.smptools.Waypoints.WaypointGUIListener;

public final class SMPTools extends JavaPlugin {

    @Override
    public void onEnable() {
        // Commands
        getCommand("waypoint").setExecutor(new WaypointCommand());
        getCommand("pdc").setExecutor(new ClearPlayerPDC());
        getCommand("coords").setExecutor(new CoordinatesBroadcast());

        // Listeners
        getServer().getPluginManager().registerEvents(new WaypointGUIListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SMPTools getInstance(){
        return getPlugin(SMPTools.class);
    }
}
