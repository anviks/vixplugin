package me.captainpotatoaim.myplugin.unfinished_commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreeperPrankWithTp extends CreeperPrank implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        } else if (!(sender instanceof Player)) {
            sender.sendMessage("Can't teleport you to a player.");
            return true;
        } else if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }

        Player target = sender.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("That player doesn't exist or is offline.");
            return true;
        }

        Location behindTarget = super.getRandomLocationBehindPlayer(target);
        Player player = (Player) sender;
        player.teleport(behindTarget);
        target.playSound(behindTarget, Sound.ENTITY_CREEPER_PRIMED, 1, 1);

        return true;
    }
}
