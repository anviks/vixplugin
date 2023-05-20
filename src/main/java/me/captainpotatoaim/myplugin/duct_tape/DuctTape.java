package me.captainpotatoaim.myplugin.duct_tape;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

public class DuctTape implements CommandExecutor {
    static HashSet<UUID> tapedPlayers = new HashSet<>();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        tapedPlayers.add(sender.getServer().getPlayer(args[0]).getUniqueId());

        return true;
    }
}
