package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SlapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length == 0) {
                Collection<? extends Player> players = sender.getServer().getOnlinePlayers();
                for (Player player : players) {
                    player.damage(2);
                    player.setVelocity(player.getFacing().getDirection().multiply(-3));
                    player.sendMessage("You just got slapped by " + sender.getName() + "!");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                    player.playSound(player.getLocation(), Sound.ENTITY_GOAT_SCREAMING_AMBIENT, 1, 1);
                }
            } else {
                for (String name : args) {
                    Player target = sender.getServer().getPlayer(name);
                    if (target == null) {
                        sender.sendMessage(name + " is not online.");
                    } else {
                        target.damage(2);
                        target.setVelocity(target.getFacing().getDirection().multiply(-3));
                        target.sendMessage("You just got slapped by " + sender.getName() + "!");
                        target.playSound(target.getLocation(), Sound.ENTITY_PARROT_IMITATE_VINDICATOR, 10, 1);
                    }
                }
            }
        } else {
            sender.sendMessage("Sadly violence is only for those willing to abuse their power. You have none.");
        }
        return true;
    }
}
