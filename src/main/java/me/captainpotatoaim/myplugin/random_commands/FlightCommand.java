package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlightCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp()) {

            Player target = null;

            if (sender instanceof Player && args.length == 0) {

                target = (Player) sender;
                target.setAllowFlight(!target.getAllowFlight());
                target.setFlying(target.getAllowFlight());

            } else if (args.length == 0) {

                sender.sendMessage(ChatColor.RED + "You need to specify a player.");
                return true;

            } else {

                target = sender.getServer().getPlayerExact(args[0]);
                if (target != null) {
                    target.setAllowFlight(!target.getAllowFlight());
                    target.setFlying(target.getAllowFlight());
                } else {
                    sender.sendMessage(ChatColor.RED + "No such player found.");
                    return true;
                }

            }

            if (target.getAllowFlight()) {
                target.sendMessage(ChatColor.GREEN + "Flying enabled.");
                if (target != sender) {
                    sender.sendMessage(ChatColor.GREEN + "Enabled flying for " + target.getDisplayName() + ".");
                }
            } else {
                target.sendMessage(ChatColor.GREEN + "Flying disabled.");
                if (target != sender) {
                    sender.sendMessage(ChatColor.GREEN + "Disabled flying for " + target.getDisplayName() + ".");
                }
            }


        } else {

            sender.sendMessage(ChatColor.RED + "YOU are only allowed to fly with elytra.");
            return true;

        }


        return true;
    }
}
