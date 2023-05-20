package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (sender.isOp()) {
                sender.sendMessage("Here's the fish you requested.");
                ItemStack salmon = new ItemStack(Material.SALMON);
                salmon.setAmount(6400);
                ((Player) sender).getInventory().addItem(salmon);
            } else {
                sender.sendMessage("Sadly, only OG-s can use that command.");
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Yo wtf dude, you lost or something? You obviously can't give fish to the console.");
        } else if (sender instanceof BlockCommandSender) {
            sender.sendMessage("Unfortunately, that's not going to work.");
        }
        return true;
    }
}
