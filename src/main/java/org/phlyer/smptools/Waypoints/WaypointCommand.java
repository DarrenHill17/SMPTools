package org.phlyer.smptools.Waypoints;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.phlyer.smptools.SMPTools;
import java.util.ArrayList;
import java.util.Collections;

public class WaypointCommand implements CommandExecutor {
    private static final NamespacedKey waypointArrayKey = new NamespacedKey(SMPTools.getPlugin(SMPTools.class), "waypoint_array");
    private static int currentPage;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage("Only players can send this command.");
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.getPersistentDataContainer().has(waypointArrayKey)){
            createInitialWaypointData(player);
        }

        menuPage(player, 0);

        return true;
    }

    public static void menuPage(Player player, int pageNumber){
        convertOldWaypoints(player);
        currentPage = pageNumber;
        ArrayList<Waypoint> waypoints = getWaypoints(player);
        Inventory inventory = Bukkit.createInventory(player, 9 * 6, Component.text("Waypoint Menu").decorate(TextDecoration.BOLD).color(TextColor.color(0x00AA00)));

        // Sign
        ItemStack pageNumberSign = new ItemStack(Material.OAK_SIGN);
        ItemMeta pageNumberSignMeta = pageNumberSign.getItemMeta();
        pageNumberSignMeta.displayName(Component.text("Page " + String.valueOf(pageNumber+1)).decoration(TextDecoration.ITALIC, false));
        pageNumberSign.setItemMeta(pageNumberSignMeta);
        inventory.setItem(49, pageNumberSign);

        // New Waypoint
        ItemStack addWaypointBlock = new ItemStack(Material.LIME_TERRACOTTA);
        ItemMeta addWaypointBlockMeta = addWaypointBlock.getItemMeta();
        addWaypointBlockMeta.displayName(Component.text("New Waypoint").decoration(TextDecoration.ITALIC, false));
        addWaypointBlock.setItemMeta(addWaypointBlockMeta);
        inventory.setItem(45, addWaypointBlock);

        // Delete all waypoints
        ItemStack deleteAllWaypointsBlock = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta deleteAllWaypointsBlockMeta = deleteAllWaypointsBlock.getItemMeta();
        deleteAllWaypointsBlockMeta.displayName(Component.text("Delete ALL Waypoints").decoration(TextDecoration.ITALIC, false).color(TextColor.color(0xAA0000)));
        deleteAllWaypointsBlock.setItemMeta(deleteAllWaypointsBlockMeta);
        inventory.setItem(53, deleteAllWaypointsBlock);

        int totalNumberOfPages = (int) Math.ceil(waypoints.size()/21.0);
        if (pageNumber != 0){
            ItemStack pageBack = new ItemStack(Material.ARROW);
            ItemMeta meta = pageBack.getItemMeta();
            meta.displayName(Component.text("Page " + String.valueOf(pageNumber)));
            pageBack.setItemMeta(meta);
            inventory.setItem(48, pageBack);
        }

        if (pageNumber < totalNumberOfPages - 1){
            ItemStack pageForward = new ItemStack(Material.ARROW);
            ItemMeta meta = pageForward.getItemMeta();
            meta.displayName(Component.text("Page " + String.valueOf(pageNumber+2)));
            pageForward.setItemMeta(meta);
            inventory.setItem(50, pageForward);
        }

        ArrayList<Waypoint> currentPageWaypoints = new ArrayList<>();
        for (int i = pageNumber * 21; i < (pageNumber + 1) * 21; i++){
            if (i >= waypoints.size()) break;
            currentPageWaypoints.add(waypoints.get(i));
        }

        int i = 0;
        int slot = 10 + i;
        for (Waypoint waypoint : currentPageWaypoints){
            ArrayList<Integer> forbiddenSlots = new ArrayList<Integer>(){
                {
                    add(17);
                    add(26);
                    add(35);
                }
            };
            if (forbiddenSlots.contains(slot) && slot != 35) slot+=2;
            else if (slot == 35) break;

            ItemStack waypointStack = new ItemStack(Material.OAK_BUTTON);
            ItemMeta meta = waypointStack.getItemMeta();
            meta.displayName(Component.text(waypoint.getName()));
            waypointStack.setItemMeta(meta);
            inventory.setItem(slot, waypointStack);
            slot++;
        }

        player.openInventory(inventory);
        player.setMetadata("OpenedGUI", new FixedMetadataValue(SMPTools.getInstance(), "WaypointGUIMain"));
    }

    private static ArrayList<Waypoint> getWaypoints(Player player){
        if (!player.getPersistentDataContainer().has(waypointArrayKey)){
            createInitialWaypointData(player);
        }
        ArrayList<Waypoint> waypoints = player.getPersistentDataContainer().get(waypointArrayKey, new WaypointDataType());
        assert waypoints != null;
        return waypoints;
    }

    public static void deleteAllWaypoints(Player player){
        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
        player.getPersistentDataContainer().set(waypointArrayKey, new WaypointDataType(), waypoints);
    }

    public static void createInitialWaypointData(Player player){
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        player.getPersistentDataContainer().set(waypointArrayKey, new WaypointDataType(), waypoints);
    }

    public static void listWaypoints(Player player){
        ArrayList<Waypoint> waypoints = player.getPersistentDataContainer().get(waypointArrayKey, new WaypointDataType());
        player.sendMessage(String.valueOf(waypoints.size()));
    }

    public static int getCurrentPage(){
        return currentPage;
    }

    public static void confirmDeleteAllWaypoints(Player player, Component[] options){
        confirmationPage(player, options, "DeleteAllConfirmationPage");
    }

    public static void confirmDeleteSpecificWaypoints(Player player, Component[] options){
        confirmationPage(player, options, "DeleteSpecificConfirmationPage");
    }

    private static void confirmationPage(Player player, Component[] options, String metadata){
        Inventory inventory = Bukkit.createInventory(player, 9 * 3, Component.text("Are you sure?"));

        ItemStack proceedBlock = new ItemStack(Material.LIME_TERRACOTTA);
        ItemMeta proceedBlockMeta = proceedBlock.getItemMeta();
        proceedBlockMeta.displayName(Component.text("Yes").decoration(TextDecoration.ITALIC, false));
        proceedBlock.setItemMeta(proceedBlockMeta);
        inventory.setItem(11, proceedBlock);

        ItemStack declineBlock = new ItemStack(Material.RED_TERRACOTTA);
        ItemMeta declineBlockMeta = declineBlock.getItemMeta();
        declineBlockMeta.displayName(Component.text("No").decoration(TextDecoration.ITALIC, false));
        declineBlock.setItemMeta(declineBlockMeta);
        inventory.setItem(15, declineBlock);


        player.openInventory(inventory);
        player.setMetadata("OpenedGUI", new FixedMetadataValue(SMPTools.getInstance(), metadata));
    }

    public static void newWaypointPage(Player player, String waypointName){
        Inventory inventory = Bukkit.createInventory(player, 9 * 3, Component.text("New Waypoint"));

        ItemStack nameSign = new ItemStack(Material.NAME_TAG);
        ItemMeta nameSignMeta = nameSign.getItemMeta();
        nameSignMeta.displayName(Component.text(waypointName).decoration(TextDecoration.ITALIC, false));
        nameSign.setItemMeta(nameSignMeta);
        inventory.setItem(11, nameSign);

        ItemStack currentLocation = new ItemStack(Material.BOOK);
        ItemMeta currentLocationMeta = currentLocation.getItemMeta();
        currentLocationMeta.displayName(Component.text("Current Location").decoration(TextDecoration.ITALIC, false));
        currentLocation.setItemMeta(currentLocationMeta);
        inventory.setItem(13, currentLocation);

        ItemStack customLocation = new ItemStack(Material.MAP);
        ItemMeta customLocationMeta = customLocation.getItemMeta();
        customLocationMeta.displayName(Component.text("Custom Location").decoration(TextDecoration.ITALIC, false));
        customLocation.setItemMeta(customLocationMeta);
        inventory.setItem(15, customLocation);

        ItemStack closePage = new ItemStack(Material.BARRIER);
        ItemMeta closePageMeta = closePage.getItemMeta();
        closePageMeta.displayName(Component.text("Close").decoration(TextDecoration.ITALIC, false).color(TextColor.color(0xFFFFFF)));
        closePage.setItemMeta(closePageMeta);
        inventory.setItem(26, closePage);

        player.openInventory(inventory);
        player.setMetadata("OpenedGUI", new FixedMetadataValue(SMPTools.getInstance(), "NewWaypointPage"));
    }

    public static boolean waypointNameExists(Player player, String name){
        ArrayList<Waypoint> waypoints = player.getPersistentDataContainer().get(waypointArrayKey, new WaypointDataType());
        for (Waypoint waypoint : waypoints){
            if (waypoint.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    public static void addNewWaypoint(Player player, Waypoint waypoint){
        ArrayList<Waypoint> waypoints = player.getPersistentDataContainer().get(waypointArrayKey, new WaypointDataType());
        assert waypoints != null;
        waypoints.add(waypoint);
        Collections.sort(waypoints);
        player.getPersistentDataContainer().set(waypointArrayKey, new WaypointDataType(), waypoints);
    }

    public static void waypointPage(Player player, Waypoint waypoint){
        Inventory inventory = Bukkit.createInventory(player, 9 * 3, Component.text("Waypoint: " + waypoint.getName()));

        ItemStack sendToPlayer = new ItemStack(Material.ELYTRA);
        ItemMeta sendToPlayerMeta = sendToPlayer.getItemMeta();
        sendToPlayerMeta.displayName(Component.text("Send to Player").decoration(TextDecoration.ITALIC, false).color(TextColor.color(0xFFFFFF)));
        sendToPlayer.setItemMeta(sendToPlayerMeta);
        inventory.setItem(11, sendToPlayer);

        ItemStack location = new ItemStack(Material.MAP);
        ItemMeta locationMeta = location.getItemMeta();
        locationMeta.displayName(Component.text(waypoint.getCoordinates()[0] + " " + waypoint.getCoordinates()[1] + " " + waypoint.getCoordinates()[2]).decoration(TextDecoration.ITALIC, false));
        TextColor color = TextColor.color(0xFFFFFF);
        switch (waypoint.getDimension()){
            case "Overworld": color = TextColor.color(0x00AA00);
            break;
            case "Nether": color = TextColor.color(0xAA0000);
            break;
            case "End": color = TextColor.color(0xAA00AA);
            break;
        }
        TextColor finalColor = color;
        ArrayList<Component> loreList = new ArrayList<Component>(){
            {
                add(Component.text(waypoint.getDimension()).color(finalColor));
            }
        };
        locationMeta.lore(loreList);
        location.setItemMeta(locationMeta);
        inventory.setItem(13, location);

        ItemStack deleteWaypoint = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta deleteWaypointMeta = deleteWaypoint.getItemMeta();
        deleteWaypointMeta.displayName(Component.text("Delete Waypoint").decoration(TextDecoration.ITALIC, false).color(TextColor.color(0xAA0000)));
        deleteWaypoint.setItemMeta(deleteWaypointMeta);
        inventory.setItem(11, deleteWaypoint);

        ItemStack closePage = new ItemStack(Material.BARRIER);
        ItemMeta closePageMeta = closePage.getItemMeta();
        closePageMeta.displayName(Component.text("Close").decoration(TextDecoration.ITALIC, false).color(TextColor.color(0xFFFFFF)));
        closePage.setItemMeta(closePageMeta);
        inventory.setItem(26, closePage);

        player.openInventory(inventory);
        player.setMetadata("OpenedGUI", new FixedMetadataValue(SMPTools.getInstance(), "WaypointPage"));
    }

    public static Waypoint getClickedWaypoint(Player player, int clickedIndex){
        ArrayList<Waypoint> waypoints = player.getPersistentDataContainer().get(waypointArrayKey, new WaypointDataType());
        assert waypoints != null;
        return waypoints.get(21*currentPage + clickedIndex);
    }

    public static void deleteWaypoint(Player player, int waypointIndex){
        ArrayList<Waypoint> waypoints = player.getPersistentDataContainer().get(waypointArrayKey, new WaypointDataType());
        assert waypoints != null;
        waypoints.remove(waypointIndex);
        Collections.sort(waypoints);
        player.getPersistentDataContainer().set(waypointArrayKey, new WaypointDataType(), waypoints);
    }

    private static void convertOldWaypoints(Player player){
        if (player.getPersistentDataContainer().getKeys().contains(new NamespacedKey(SMPTools.getPlugin(SMPTools.class), "saved-waypoint-keys"))){
            String[] keys = player.getPersistentDataContainer().get(new NamespacedKey(SMPTools.getPlugin(SMPTools.class), "saved-waypoint-keys"), PersistentDataType.STRING).split("#");
            for (String key : keys){
                if (key != null && !(key.isEmpty())){
                    String[] coords = player.getPersistentDataContainer().get(new NamespacedKey(SMPTools.getPlugin(SMPTools.class), key), PersistentDataType.STRING).split("#");
                    int[] coordsInts = {Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])};
                    addNewWaypoint(player, new Waypoint(key, coordsInts, World.Environment.NORMAL));
                    player.getPersistentDataContainer().remove(new NamespacedKey(SMPTools.getPlugin(SMPTools.class), key));
                }
            }
            player.getPersistentDataContainer().remove(new NamespacedKey(SMPTools.getPlugin(SMPTools.class), "saved-waypoint-keys"));
        }
    }
}
