package com.github.anviks.vixplugin.commands;

import com.github.anviks.vixplugin.listeners.JoinMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class Freeze implements CommandExecutor {

    private final Plugin plugin;
    public static HashMap<UUID, Integer> frozenPlayers = new HashMap<>();

    public Freeze(Plugin plugin) {
        this.plugin = plugin;
    }

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
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1_892_160_000, 0));
                target.setFreezeTicks(Integer.MAX_VALUE);
                sender.sendMessage(target.displayName().append(text(" has been frozen.")).color(AQUA).decorate(BOLD));
                target.sendMessage(text("You have been frozen.", AQUA, BOLD));
                return true;
            }

            if (!target.hasPermission("vix.move")) {
                sender.sendMessage(target.displayName().append(text(" is already frozen.")).color(RED));
                return true;
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
                case "s":
                    ticks *= 20;
                case "m":
                    ticks *= 60;
                case "h":
                    ticks *= 60;
                case "d":
                    ticks *= 24;
                case "mo":
                    ticks *= 30;
            }

            PermissionAttachment attachment = JoinMessage.permissions.get(target.getUniqueId());
            attachment.setPermission("vix.move", false);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 0));
            target.setFreezeTicks(Integer.MAX_VALUE);
            sender.sendMessage(target.displayName().append(text(" has been frozen.")).color(AQUA).decorate(BOLD));
            target.sendMessage(text("You have been frozen.", AQUA, BOLD));

            int unfreezeTask = Bukkit.getScheduler()
                    .scheduleSyncDelayedTask(plugin, () -> {
                        attachment.setPermission("vix.move", true);
                        target.setFreezeTicks(100);
                    }, ticks);

        } else {
            sender.sendMessage(text("You don't have permission to use this command.", RED));
        }

        return true;
    }
}
