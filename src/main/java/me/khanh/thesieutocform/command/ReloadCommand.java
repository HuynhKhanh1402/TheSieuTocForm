package me.khanh.thesieutocform.command;

import lombok.Getter;
import me.khanh.thesieutocform.TheSieuTocFormPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


/**
 * CommandExecutor implementation for the reload command.
 * This class handles the logic for reloading the plugin.
 */
public class ReloadCommand implements CommandExecutor {
    @Getter
    private final TheSieuTocFormPlugin plugin;

    public ReloadCommand(TheSieuTocFormPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(ChatColor.AQUA + "Reloading plugin");
        try {
            plugin.reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "Reloaded plugin");
        } catch (Exception e){
            sender.sendMessage(ChatColor.RED + "An error occurred while reloading plugin:");
            sender.sendMessage(ChatColor.RED + e.getClass().getPackageName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
}
