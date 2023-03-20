package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class LaunchCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length == 0) {
                Collection<? extends Player> players = sender.getServer().getOnlinePlayers();
                for (Player player : players) {
                    player.damage(1);
                    player.setVelocity(new Vector(0, 100, 0));
                    player.sendMessage(
                            (player.getName().equals(sender.getName())) ?
                                    "I believe I can fly." :
                                    sender.getName() + " believes you can fly.");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
                }
            } else {
                for (String name : args) {
                    Player target = sender.getServer().getPlayer(name);
                    if (target == null) {
                        sender.sendMessage(name + " is not online.");
                    } else {
                        target.damage(1);
                        target.setVelocity(new Vector(0, 100, 0));
                        target.sendMessage(
                                (target.getName().equals(sender.getName())) ?
                                        "I believe I can fly." :
                                        sender.getName() + " believes you can fly.");
                        target.playSound(target.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
                    }
                }
            }
        } else {
            sender.sendMessage("Yea, right.");
        }
        return true;
    }
}
