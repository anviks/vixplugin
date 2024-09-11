package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChangeWorlds implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && sender instanceof Player player) {
            if (args.length > 0) {
                player.teleport(player.getServer().getWorld(args[0]).getSpawnLocation());
            } else {
                player.sendMessage("Enter a world name.");
            }
        }

        return true;
    }
}
