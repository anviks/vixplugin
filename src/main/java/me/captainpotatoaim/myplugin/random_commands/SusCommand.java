package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (sender) {
            case Player player -> {
                if (sender.isOp()) {
                    sender.sendMessage("Here's the fish you requested.");
                    ItemStack salmon = new ItemStack(Material.SALMON);
                    salmon.setAmount(6400);
                    player.getInventory().addItem(salmon);
                } else {
                    sender.sendMessage("Sadly, only OG-s can use that command.");
                }
            }
            case ConsoleCommandSender consoleCommandSender ->
                    consoleCommandSender.sendMessage("Yo wtf dude, you lost or something? You obviously can't give fish to the console.");
            case BlockCommandSender blockCommandSender ->
                    blockCommandSender.sendMessage("Unfortunately, that's not going to work.");
            default -> {
            }
        }
        return true;
    }
}
