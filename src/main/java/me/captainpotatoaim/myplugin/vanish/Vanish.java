package me.captainpotatoaim.myplugin.vanish;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class Vanish implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && sender instanceof Player player) {
            // TODO: Check if player was invisible before
            if (!player.isInvisible()) {
                player.setInvisible(true);
                var event = new PlayerQuitEvent(player, "Doesn't matter");
                Bukkit.getPluginManager().callEvent(event);
            } else {
                player.setInvisible(false);
                var event = new PlayerLoginEvent(player, "bro idk", InetAddress.getLoopbackAddress(), InetAddress.getLoopbackAddress());
                Bukkit.getPluginManager().callEvent(event);
            }
        }

        return true;
    }
}
