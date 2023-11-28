package org.phlyer.smptools.AdminCommands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ClearPlayerPDC implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1){
            commandSender.sendMessage(Component.text(Bukkit.getPlayer(strings[0]).getPersistentDataContainer().toString()));
        } else if (strings.length == 2 && strings[1].equalsIgnoreCase("keys")) {
            commandSender.sendMessage(Bukkit.getPlayer(strings[0]).getPersistentDataContainer().getKeys().toString());
        } else if (strings.length == 2 && strings[1].equalsIgnoreCase("clear")) {
            Set<NamespacedKey> keys = Bukkit.getPlayer(strings[0]).getPersistentDataContainer().getKeys();
            for (NamespacedKey key : keys){
                Bukkit.getPlayer(strings[0]).getPersistentDataContainer().remove(key);
            }
        }

        return true;
    }
}
