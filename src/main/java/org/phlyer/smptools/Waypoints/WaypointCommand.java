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
import org.jetbrains.annotations.NotNull;
import org.phlyer.smptools.SMPTools;

import javax.xml.stream.events.Namespace;
import java.lang.reflect.Array;
import java.util.ArrayList;

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
        System.out.println(waypoints);
        assert waypoints != null;
        return waypoints;
    }

    public static void deleteAllWaypoints(Player player){
        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
        player.getPersistentDataContainer().set(waypointArrayKey, new WaypointDataType(), waypoints);
    }

    public static void createInitialWaypointData(Player player){
        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>(){
            {
                add(new Waypoint("Test1", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test2", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test3", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test4", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test5", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test6", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test7", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test8", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test9", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test10", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test11", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test12", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test13", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test14", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test15", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test16", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test17", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test18", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test19", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test20", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test21", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test22", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test23", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test24", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test25", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test26", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test27", new int[]{1, 2, 3}, World.Environment.NORMAL));
                add(new Waypoint("Test28", new int[]{1, 2, 3}, World.Environment.NORMAL));

            }
        };
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

        // 11, 13, 15
        ItemStack nameSign = new ItemStack(Material.OAK_SIGN);
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
        ItemMeta closePageMeta = customLocation.getItemMeta();
        closePageMeta.displayName(Component.text("Close").decoration(TextDecoration.ITALIC, false).color(TextColor.color(0xFFFFFF)));
        closePage.setItemMeta(closePageMeta);
        inventory.setItem(26, closePage);

        player.openInventory(inventory);
        player.setMetadata("OpenedGUI", new FixedMetadataValue(SMPTools.getInstance(), "NewWaypointPage"));
    }
}
