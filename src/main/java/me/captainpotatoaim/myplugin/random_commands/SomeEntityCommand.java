package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;

public class SomeEntityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {
            Giant entity = (Giant) player.getWorld().spawnEntity(player.getLocation(), EntityType.GIANT);

            entity.setAI(true);
        }




        return true;
    }
}
