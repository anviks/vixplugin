package me.captainpotatoaim.myplugin.unfinished_commands;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.listeners.JoinMessage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.Permissions;
import java.util.List;

public class GivePermission implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp() && args.length > 1) {
            Player player = sender.getServer().getPlayerExact(args[1]);

            if (player != null) {

                Plugin plugin = JavaPlugin.getPlugin(Initializer.class);

                List<String> perms = plugin.getDescription()
                        .getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .toList();

                switch (args[0]) {

                    case "set" -> {
                        if (perms.contains(args[2])) {
                            PermissionAttachment attachment = JoinMessage.permissions.get(player.getUniqueId());
                            attachment.setPermission(args[2], Boolean.parseBoolean(args[3]));
                        } else {
                            sender.sendMessage("That permission doesn't exist.");
                        }
                    }

                    case "unset" -> {
                        if (perms.contains(args[2])) {
                            PermissionAttachment attachment = JoinMessage.permissions.get(player.getUniqueId());
                            attachment.unsetPermission(args[2]);
                        } else {
                            sender.sendMessage("That permission doesn't exist.");
                        }
                    }

                    case "get" -> {
                        PermissionAttachment attachment = JoinMessage.permissions.get(player.getUniqueId());
                        sender.sendMessage(attachment.getPermissions().toString());
                    }
                }

            } else {
                sender.sendMessage("Does that player exist?");
            }
        } else if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        switch (args.length) {

            case 1 -> {
                return List.of("get", "set", "unset");
            }
            case 2 -> {
                return sender.getServer()
                        .getOnlinePlayers()
                        .stream()
                        .map(Player::getDisplayName)
                        .toList();
            }
            case 3 -> {
                Plugin plugin = JavaPlugin.getPlugin(Initializer.class);

                return plugin.getDescription()
                        .getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .toList();
            }
            case 4 -> {
                return List.of("true", "false");
            }

        }

        return null;

    }
}
