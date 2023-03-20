package me.captainpotatoaim.myplugin.sandbox;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SandboxJoinCommand {

    public static HashMap<UUID, SandboxPlayerData> sandboxedPlayers = new HashMap<>();

    public static boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        World destination = null;
        Player player = null;
        if (args.length > 2) {
            player = sender.getServer().getPlayerExact(args[2]);
        } else if (args.length == 2){
            player = (Player) sender;
        } else {
            return false;
        }

        if (args[1].startsWith("slot-")) {

            try {
                destination = SandboxCreateCommand.worlds.get(Integer.parseInt(args[1].substring(5)));
                if (destination == null) {
                    sender.sendMessage(ChatColor.RED
                            + String.format("You don't have a sandbox in slot "
                            + args[1].substring(5)
                            + ". You only have sandboxes in slots "
                            + SandboxCreateCommand.worlds.keySet()));
                    return true;
                }

            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + args[1].substring(5) + " is not a number.");
                return true;
            }

        } else if (args[1].startsWith("sandbox-")) {

            for (int id : SandboxCreateCommand.worlds.keySet()) {
                if (SandboxCreateCommand.worlds.get(id).getName().equals(args[1])) {
                    destination = SandboxCreateCommand.worlds.get(id);
                    break;
                }
            }

            if (destination == null) {
                sender.sendMessage(ChatColor.RED + args[1] + " doesn't exist.");
                return true;
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Incorrect syntax.");
            return true;
        }

        if (player == null) {

            if (!args[2].equals("all")) {
                // TODO: If not in sandbox already
                sender.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }

            for (Player person : sender.getServer().getOnlinePlayers()) {
                args[2] = person.getName();
                onCommand(sender, command, label, args);
            }

            return true;
        }

        if (Initializer.defaultWorlds.contains(player.getWorld())) {
            sandboxedPlayers.put(player.getUniqueId(), new SandboxPlayerData(player));
        }

        player.teleport(destination.getSpawnLocation());
        player.setBedSpawnLocation(destination.getSpawnLocation(), true);

        return true;
    }
}
