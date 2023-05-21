package me.captainpotatoaim.myplugin.unfinished_commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class GigaChest implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && sender instanceof Player player) {
            Inventory gigaChest = Bukkit.createInventory(player, 54, ChatColor.GREEN + "GIGACHEST!!!");
            player.openInventory(gigaChest);
        }

        return true;
    }
}
