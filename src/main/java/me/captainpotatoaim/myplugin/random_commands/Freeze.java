package me.captainpotatoaim.myplugin.random_commands;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.captainpotatoaim.myplugin.listeners.JoinMessage;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class Freeze implements CommandExecutor {

    // Stores players that have been frozen and the scheduled task id, that
    public static HashMap<UUID, Integer> frozenPlayers = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Nothing happened.");
            return false;
        }

        Player target = sender.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("That player doesn't exist or isn't online.");
            return true;
        }

        if (sender.hasPermission("vix.moderate")) {

            if (args.length == 1) {
                PermissionAttachment attachment = JoinMessage.permissions.get(target.getUniqueId());
                attachment.setPermission("vix.move", false);
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1_892_160_000, 0));
                target.setFreezeTicks(Integer.MAX_VALUE);
                sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + target.getDisplayName() + " has been frozen.");
                target.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "You have been frozen.");
                return true;
            }

            if (!target.hasPermission("vix.move")) {
                sender.sendMessage(ChatColor.RED + "That player is already frozen.");
            }

            int duration = 0;
            String unit = "";

            for (int i = 0; i < args[1].length(); i++) {
                if (!(args[1].charAt(i) >= '0' && args[1].charAt(i) <= '9')) {
                    duration = Integer.parseInt(args[1].substring(0, i));
                    unit = args[1].substring(i);
                    break;
                }
            }

            int ticks = duration;

            switch (unit) {
                case "s": ticks *= 20;
                case "m": ticks *= 60;
                case "h": ticks *= 60;
                case "d": ticks *= 24;
                case "mo": ticks *= 30;
            }

            PermissionAttachment attachment = JoinMessage.permissions.get(target.getUniqueId());
            attachment.setPermission("vix.move", false);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ticks, 0));
            target.setFreezeTicks(Integer.MAX_VALUE);
            sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + target.getDisplayName() + " has been frozen.");
            target.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "You have been frozen.");

            int unfreezeTask = sender.getServer()
                    .getScheduler()
                    .scheduleSyncDelayedTask(JavaPlugin.getPlugin(Initializer.class), () -> {
                        attachment.setPermission("vix.move", true);
                        target.setFreezeTicks(100);
                    }, ticks);

        } else {

            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");

        }


        return true;
    }
}
