package me.captainpotatoaim.myplugin.teleport_arrows;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveTeleportArrow implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp() || args.length == 0 || args.length == 1 && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "fuck you");
            return true;
        }

        Player player;

        if (args.length == 1) {
            player = (Player) sender;
        } else {
            player = sender.getServer().getPlayerExact(args[1]);
        }

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "fuck you");
            return true;
        }

        try {
            player.getInventory().addItem(TeleportArrow.tpArrow(Integer.parseInt(args[0])));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Enter a fucking number plz.");
        }

        return true;
    }
}
