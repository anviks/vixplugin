package me.captainpotatoaim.myplugin.custom_items.railgun;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GiveRailgun implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Bro, you ain't OP.");
            return true;
        }

        if (args.length == 0) {
            if (sender instanceof Player player) {
                player.getInventory().addItem(Railgun.getItem());
            } else {
                sender.sendMessage(ChatColor.RED + "Where is the railgun supposed to be placed? "
                        + sender.getName() + " doesn't have an inventory.");
            }
        } else {
            Player player = sender.getServer().getPlayerExact(args[0]);
            if (player != null) {
                player.getInventory().addItem(Railgun.getItem());
            } else {
                sender.sendMessage(ChatColor.RED + "Enter a valid name please.");
            }
        }

        return true;
    }
}
