package me.captainpotatoaim.myplugin.random_commands;

import me.captainpotatoaim.myplugin.listeners.JoinMessage;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class UnFreeze implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("vix.moderate")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        Player target = sender.getServer().getPlayerExact(args[0]);
        Object[] offlinePlayer = Arrays.stream(sender.getServer()
                        .getOfflinePlayers())
                        .map(OfflinePlayer::getName)
                        .filter(e -> args[0].equalsIgnoreCase(e))
                        .toArray();

        if (offlinePlayer.length > 0 && target == null) {
            sender.sendMessage(ChatColor.RED + offlinePlayer[0].toString() + " isn't online at the moment.");
            return true;
        }

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "That player doesn't exist.");
            return true;
        }

        PermissionAttachment attachment = JoinMessage.permissions.get(target.getUniqueId());
        attachment.setPermission("vix.move", true);
        target.removePotionEffect(PotionEffectType.SLOW);
        target.setFreezeTicks(100);
        target.getServer().getScheduler().cancelTask(1);

        return true;
    }
}
