package com.github.anviks.vixplugin.commands;

import com.github.anviks.vixplugin.listeners.JoinMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class UnFreeze implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("vix.moderate")) {
            sender.sendMessage(text("You don't have permission to use this command.", RED));
            return true;
        }

        Player target = sender.getServer().getPlayerExact(args[0]);
        Object[] offlinePlayer = Arrays.stream(sender.getServer()
                        .getOfflinePlayers())
                        .map(OfflinePlayer::getName)
                        .filter(e -> args[0].equalsIgnoreCase(e))
                        .toArray();

        if (offlinePlayer.length > 0 && target == null) {
            sender.sendMessage(text(offlinePlayer[0].toString() + " isn't online at the moment.", RED));
            return true;
        }

        if (target == null) {
            sender.sendMessage(text("That player doesn't exist.", RED));
            return true;
        }

        PermissionAttachment attachment = JoinMessage.permissions.get(target.getUniqueId());
        attachment.setPermission("vix.move", true);
        target.removePotionEffect(PotionEffectType.SLOWNESS);
        target.setFreezeTicks(100);
        target.getServer().getScheduler().cancelTask(1);

        return true;
    }
}
