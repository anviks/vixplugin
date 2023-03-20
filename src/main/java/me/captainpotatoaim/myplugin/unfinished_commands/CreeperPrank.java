package me.captainpotatoaim.myplugin.unfinished_commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class CreeperPrank implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        } else if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }

        Player target = sender.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("That player doesn't exist or is offline.");
            return true;
        }

        Location behindTarget = getRandomLocationBehindPlayer(target);
        target.playSound(behindTarget, Sound.ENTITY_CREEPER_PRIMED, 1, 1);

        return true;
    }

    protected Location getRandomLocationBehindPlayer(Player target) {
        int angle = Math.random() <= 0.5 ? -90 : 90;

        Vector direction = target.getFacing()
                .getDirection()
                .subtract(
                        target.getFacing()
                                .getDirection()
                                .multiply(Math.random() * 10)
                );

        direction.add(target.getFacing()
                .getDirection()
                .rotateAroundY(angle)
                .multiply(Math.random() * 10));

        return direction.toLocation(target.getWorld());
    }
}
