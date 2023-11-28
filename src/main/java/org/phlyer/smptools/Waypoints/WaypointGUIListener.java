package org.phlyer.smptools.Waypoints;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.phlyer.smptools.SMPTools;

import java.util.Objects;

public class WaypointGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if (player.hasMetadata("OpenedGUI")){
            event.setCancelled(true);
            if (player.getMetadata("OpenedGUI").get(0).asString().equalsIgnoreCase("WaypointGUIMain")){
                if (event.getSlot() == 49){
                    WaypointCommand.listWaypoints(player);
                }
                else if (event.getSlot() == 45){
                    WaypointCommand.newWaypointPage(player, "Waypoint Name");
                }
                else if (event.getSlot() == 50 && Objects.equals(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().displayName(), Component.text("Page " + String.valueOf(WaypointCommand.getCurrentPage() + 2)))){
                    WaypointCommand.menuPage(player, WaypointCommand.getCurrentPage()+1);
                }
                else if (event.getSlot() == 48 && Objects.equals(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().displayName(), Component.text("Page " + String.valueOf(WaypointCommand.getCurrentPage())))){
                    WaypointCommand.menuPage(player, WaypointCommand.getCurrentPage()-1);
                }
                else if(event.getSlot() == 53){
                    Component[] choices = {Component.text("Yes"), Component.text("No")};
                    WaypointCommand.confirmDeleteAllWaypoints(player, choices);
                }
            }
            else if (player.getMetadata("OpenedGUI").get(0).asString().equalsIgnoreCase("DeleteAllConfirmationPage")){
                if (event.getSlot() == 11){
                    WaypointCommand.deleteAllWaypoints(player);
                    WaypointCommand.menuPage(player, 0);
                    player.sendMessage(Component.text("All waypoints deleted."));
                }
                else if (event.getSlot() == 15){
                    WaypointCommand.menuPage(player, WaypointCommand.getCurrentPage());
                }
            }
            else if(player.getMetadata("OpenedGUI").get(0).asString().equalsIgnoreCase("NewWaypointPage")){
                if (event.getSlot() == 26){
                    WaypointCommand.menuPage(player, WaypointCommand.getCurrentPage());
                }
                else if (event.getSlot() == 11){

                }
                else if (event.getSlot() == 13){

                }
                else if (event.getSlot() == 15){

                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();

        if (player.hasMetadata("OpenedGUI")) player.removeMetadata("OpenedGUI", SMPTools.getInstance());
    }
}
