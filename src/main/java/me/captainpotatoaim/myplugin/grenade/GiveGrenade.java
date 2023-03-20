package me.captainpotatoaim.myplugin.grenade;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveGrenade implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp() && sender instanceof Player player) {
            player.getInventory().addItem(GrenadeItem.grenadeItem(Integer.parseInt(args[0])));
        }



        return true;
    }
}
