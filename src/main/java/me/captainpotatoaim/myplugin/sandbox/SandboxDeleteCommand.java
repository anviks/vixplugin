package me.captainpotatoaim.myplugin.sandbox;

import me.captainpotatoaim.myplugin.Initializer;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class SandboxDeleteCommand {

    public static boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            return false;
        }

        int slot = -1;

        if (args[1].startsWith("slot-")) {

            try {
                slot = Integer.parseInt(args[1].substring(5));
                if (!SandboxCreateCommand.worlds.containsKey(slot)) {
                    if (SandboxCreateCommand.worlds.size() > 0) {
                        sender.sendMessage(ChatColor.RED + "You only have sandboxes in slots " + SandboxCreateCommand.worlds.keySet());
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have any sandboxes. Create one using /sandbox create.");
                    }
                    return true;
                }

            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + args[1] + " is not a number.");
                return true;
            }

        } else if (args[1].startsWith("sandbox-")) {

            for (int id : SandboxCreateCommand.worlds.keySet()) {
                if (SandboxCreateCommand.worlds.get(id).getName().equals(args[1])) {
                    slot = id;
                    break;
                }
            }

            if (slot == -1) {
                sender.sendMessage(ChatColor.RED + args[1] + " doesn't exist.");
                return true;
            }

        } else {

            sender.sendMessage(ChatColor.RED + "Incorrect syntax.");
            return true;

        }

        // TODO: teleport everyone out

        World deletedWorld = SandboxCreateCommand.worlds.get(slot);
        Bukkit.unloadWorld(deletedWorld, false);
        sender.sendMessage(ChatColor.YELLOW + String.format("Deleting %s...", deletedWorld.getName()));
        SandboxCreateCommand.worlds.remove(slot);

        Runnable runnable = () -> {
            try {
                FileUtils.deleteDirectory(deletedWorld.getWorldFolder());
                sender.sendMessage(ChatColor.GREEN + String.format("Successfully deleted %s.", deletedWorld.getName()));
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + String.format("Failed to delete %s!", deletedWorld.getName()));
                throw new RuntimeException(e);
            }
        };

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Initializer.plugin, runnable, 1);


        return true;
    }
}
