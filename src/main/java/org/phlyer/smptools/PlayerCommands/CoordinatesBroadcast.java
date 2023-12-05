package org.phlyer.smptools.PlayerCommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoordinatesBroadcast implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0 && commandSender instanceof Player){
            Player player = (Player) commandSender;
            Bukkit.broadcast(getCoordsText(player));
        } else if (strings.length == 1 && commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (Bukkit.getPlayer(strings[0]) == null){
                player.sendMessage(Component.text(strings[0] + " is not online.").color(TextColor.color(0xAA0000)));
            }
            else {
                Bukkit.getPlayer(strings[0]).sendMessage(getCoordsText(player));
            }
        }
        return true;
    }

    private Component getCoordsText(Player sender){
        Component dimension = Component.text("");
        switch (sender.getLocation().getWorld().getEnvironment()){
            case NORMAL: dimension = Component.text("\nOverworld").color(TextColor.color(0xFFFFFF));
                break;
            case NETHER: dimension = Component.text("\nNether").color(TextColor.color(0xFFFFFF));
                break;
            case THE_END: dimension = Component.text("\nEnd").color(TextColor.color(0xFFFFFF));
                break;
        }
        return Component.text(sender.getName() + "'s Coordinates")
                .decoration(TextDecoration.UNDERLINED, false)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.color(0xFFAA00))
                .append(Component.text("\n" + sender.getLocation().getBlockX() + " " + sender.getLocation().getBlockY() + " " + sender.getLocation().getBlockZ())
                        .decoration(TextDecoration.UNDERLINED, false)
                        .decoration(TextDecoration.BOLD, false)
                        .decoration(TextDecoration.ITALIC, false)
                        .color(TextColor.color(0xFFFFFF)))
                .append(dimension.decoration(TextDecoration.UNDERLINED, false)
                        .decoration(TextDecoration.BOLD, false)
                        .decoration(TextDecoration.ITALIC, false));
    }
}
