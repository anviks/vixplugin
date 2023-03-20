package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodMode implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp()) {

            Player target = null;

            if (sender instanceof Player && args.length == 0) {
                target = (Player) sender;
                target.setInvulnerable(!target.isInvulnerable());

            } else if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "You need to specify a player.");
                return true;
            } else {
                target = sender.getServer().getPlayerExact(args[0]);
                if (target != null) {
                    target.setInvulnerable(!target.isInvulnerable());
                } else {
                    sender.sendMessage(ChatColor.RED + "No such player found.");
                    return true;
                }
            }

            if (target.isInvulnerable()) {
                target.sendMessage(ChatColor.GREEN + "You are now in god mode.");
                if (target != sender) {
                    sender.sendMessage(ChatColor.GREEN + target.getDisplayName() + " is now in god mode.");
                }
            } else {
                target.sendMessage(ChatColor.GREEN + "You are no longer in god mode.");
                if (target != sender) {
                    sender.sendMessage(ChatColor.GREEN + target.getDisplayName() + " is no longer in god mode.");
                }
            }


        } else {

            sender.sendMessage(ChatColor.RED + "You lack the divinity to use this command.");
            return true;

        }



        return true;
    }
}
