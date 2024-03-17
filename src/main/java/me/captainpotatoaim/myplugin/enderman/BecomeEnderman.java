package me.captainpotatoaim.myplugin.enderman;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BecomeEnderman implements CommandExecutor {
    static Set<UUID> endermenPlayers = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && sender instanceof Player player) {
            endermenPlayers.add(player.getUniqueId());
        }

        return true;
    }
}
