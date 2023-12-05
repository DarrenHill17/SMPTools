package org.phlyer.smptools.Waypoints;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.phlyer.smptools.SMPTools;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class WaypointGUIListener implements Listener {
    private String createWaypointLastName = "Waypoint Name";
    private int lastWaypointOpenedWaypointIndex;
    private String lastRecipientSelected;

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if (player.hasMetadata("OpenedGUI")){
            event.setCancelled(true);
            if (player.getMetadata("OpenedGUI").get(0).asString().equalsIgnoreCase("WaypointGUIMain")){
                 if (event.getSlot() == 45){
                    WaypointCommand.newWaypointPage(player, createWaypointLastName);
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
                else{
                    if (event.getClickedInventory().getItem(event.getSlot()).getType() == Material.OAK_BUTTON){
                        int clickedIndex;
                        if (event.getSlot() <= 16) clickedIndex = event.getSlot() - 10;
                        else if (event.getSlot() <= 25) clickedIndex = event.getSlot() - 12;
                        else clickedIndex = event.getSlot() - 14;
                        WaypointCommand.waypointPage(player, WaypointCommand.getClickedWaypoint(player, clickedIndex));
                        lastWaypointOpenedWaypointIndex = 21*WaypointCommand.getCurrentPage() + clickedIndex;
                    }
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
            else if (player.getMetadata("OpenedGUI").get(0).asString().equalsIgnoreCase("DeleteSpecificConfirmationPage")){
                if (event.getSlot() == 11){
                    WaypointCommand.deleteWaypoint(player, lastWaypointOpenedWaypointIndex);
                    WaypointCommand.menuPage(player, 0);
                    player.sendMessage(Component.text("Waypoint deleted."));
                }
                else if (event.getSlot() == 15){
                    WaypointCommand.waypointPage(player, WaypointCommand.getClickedWaypoint(player, lastWaypointOpenedWaypointIndex));
                }
            }
            else if(player.getMetadata("OpenedGUI").get(0).asString().equalsIgnoreCase("NewWaypointPage")){
                if (event.getSlot() == 26){
                    WaypointCommand.menuPage(player, WaypointCommand.getCurrentPage());
                    createWaypointLastName = "Waypoint Name";
                }
                else if (event.getSlot() == 11){
                    openNewWaypointName(player);
                }
                else if (event.getSlot() == 13){
                    Location location = player.getLocation();
                    int[] coordinate = {location.getBlockX(), location.getBlockY(), location.getBlockZ()};
                    if (createWaypointLastName.equalsIgnoreCase("Waypoint Name")) openNewWaypointName(player);
                    WaypointCommand.addNewWaypoint(player, new Waypoint(createWaypointLastName, coordinate, location.getWorld().getEnvironment()));
                    WaypointCommand.menuPage(player, 0);
                    createWaypointLastName = "Waypoint Name";
                }
                else if (event.getSlot() == 15){
                    player.sendMessage(Component.text("Feature not currently supported."));
                }
            }
            else if (player.getMetadata("OpenedGUI").get(0).asString().equalsIgnoreCase("WaypointPage")) {
                if (event.getSlot() == 26){
                    WaypointCommand.menuPage(player, WaypointCommand.getCurrentPage());
                }
                else if (event.getSlot() == 16){
                    Component[] choices = {Component.text("Yes"), Component.text("No")};
                    WaypointCommand.confirmDeleteSpecificWaypoints(player, choices);
                }
                else if (event.getSlot() == 10){
                    Waypoint waypoint = WaypointCommand.getClickedWaypoint(player, lastWaypointOpenedWaypointIndex);
                    TextColor color;
                    switch (waypoint.getDimension()){
                        case "Overworld": color = TextColor.color(0x00AA00);
                            break;
                        case "Nether": color = TextColor.color(0xAA0000);
                            break;
                        case "End": color = TextColor.color(0xAA00AA);
                            break;
                        default: color = TextColor.color(0xFFFFFF);
                    }
                    TextColor finalColor = color;
                    player.sendMessage(Component.text(waypoint.getName()).decoration(TextDecoration.BOLD, true).color(TextColor.color(0xFFAA00)));
                    player.sendMessage(Component.text(waypoint.getCoordinates()[0] + " " + waypoint.getCoordinates()[1] + " " + waypoint.getCoordinates()[2]));
                    player.sendMessage(Component.text(waypoint.getDimension()).color(color));
                }
                else if (event.getSlot() == 14){
                    WaypointCommand.onlinePlayers(player, 0);
                }
                else if (event.getSlot() == 12){
                    openWaypointRename(player, WaypointCommand.getClickedWaypoint(player, lastWaypointOpenedWaypointIndex), lastWaypointOpenedWaypointIndex);
                }
            }
            else if (player.getMetadata("OpenedGUI").get(0).asString().equalsIgnoreCase("RecipientPage")) {
                if (event.getSlot() == 50 && Objects.equals(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().displayName(), Component.text("Page " + String.valueOf(WaypointCommand.getCurrentPlayerPage() + 2)))){
                    WaypointCommand.onlinePlayers(player, WaypointCommand.getCurrentPlayerPage()+1);
                }
                else if (event.getSlot() == 48 && Objects.equals(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().displayName(), Component.text("Page " + String.valueOf(WaypointCommand.getCurrentPlayerPage())))){
                    WaypointCommand.onlinePlayers(player, WaypointCommand.getCurrentPlayerPage()-1);
                }
                else if (event.getSlot() == 53){
                    WaypointCommand.menuPage(player, WaypointCommand.getCurrentPage());
                }
                else if (event.getClickedInventory().getItem(event.getSlot()).getType() == Material.PLAYER_HEAD){
                    lastRecipientSelected = event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName();
                    if (WaypointCommand.sendWaypoint(player, lastRecipientSelected, WaypointCommand.getClickedWaypoint(player, lastWaypointOpenedWaypointIndex))){
                        player.sendMessage(Component.text("Waypoint " + WaypointCommand.getClickedWaypoint(player, lastWaypointOpenedWaypointIndex).getName() + " sent."));
                    }
                    else {
                        if (Bukkit.getPlayer(lastRecipientSelected) == null) player.sendMessage(Component.text(lastRecipientSelected + " is not online."));
                        else player.sendMessage(Component.text("Unable to send waypoint as recipient already has a waypoint named " + WaypointCommand.getClickedWaypoint(player, lastWaypointOpenedWaypointIndex).getName() + "."));
                    }
                    WaypointCommand.menuPage(player, 0);
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();

        if (player.hasMetadata("OpenedGUI")) player.removeMetadata("OpenedGUI", SMPTools.getInstance());
    }

    private void openNewWaypointName(Player player){
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    createWaypointLastName = stateSnapshot.getText().trim();
                    WaypointCommand.newWaypointPage(player, createWaypointLastName);
                })
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    if(!WaypointCommand.waypointNameExists(player, stateSnapshot.getText().trim()) && !stateSnapshot.getText().trim().equalsIgnoreCase("Waypoint Name")) {
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } else if (stateSnapshot.getText().trim().equalsIgnoreCase("Waypoint Name")) {
                        player.sendMessage(Component.text("Enter the waypoint name.").color(TextColor.color(0xAA0000)));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Waypoint Name"));
                    } else {
                        player.sendMessage(Component.text("Waypoint name already exists.").color(TextColor.color(0xAA0000)));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Waypoint Name"));
                    }
                })
                .preventClose()
                .text(createWaypointLastName)
                .title("Enter waypoint name")
                .plugin(SMPTools.getInstance())
                .open(player);
    }

    private void openWaypointRename(Player player, Waypoint waypoint, int index){
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    World.Environment dimension = World.Environment.NORMAL;
                    switch (waypoint.getDimension()){
                        case "Nether": dimension = World.Environment.NETHER;
                            break;
                        case "End": dimension = World.Environment.THE_END;
                            break;
                    }
                    Waypoint newWaypoint = new Waypoint(stateSnapshot.getText().trim(), waypoint.getCoordinates(), dimension);
                    WaypointCommand.deleteWaypoint(player, index);
                    WaypointCommand.addNewWaypoint(player, newWaypoint);
                    WaypointCommand.menuPage(player, 0);
                    player.sendMessage(Component.text("Waypoint Renamed."));
                })
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    if(!WaypointCommand.waypointNameExists(player, stateSnapshot.getText().trim()) && !stateSnapshot.getText().trim().equalsIgnoreCase(waypoint.getName())) {
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } else if (stateSnapshot.getText().trim().equalsIgnoreCase(waypoint.getName())) {
                        player.sendMessage(Component.text("Enter the new waypoint name.").color(TextColor.color(0xAA0000)));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(waypoint.getName()));
                    } else {
                        player.sendMessage(Component.text("Waypoint name already exists.").color(TextColor.color(0xAA0000)));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(waypoint.getName()));
                    }
                })
                .preventClose()
                .text(waypoint.getName())
                .title("Enter waypoint name")
                .plugin(SMPTools.getInstance())
                .open(player);
    }
}
