package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeleportUp implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && sender instanceof Player player) {
            player.teleport(getRandomLocationAbove(player));
        }

        return true;
    }

    private Location getRandomLocationAbove(Player player) {
        Location playerLocation = player.getLocation();
        double height = playerLocation.getY();
        double maxHeight = 319;
        double destinationHeight = Math.random() * (maxHeight - height);
        playerLocation.setY(height + destinationHeight);
        System.out.println(player.getLocation());
        System.out.println(destinationHeight);
        System.out.println(playerLocation);

        return playerLocation;
    }
}
