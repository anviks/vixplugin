package me.captainpotatoaim.myplugin.explosive_arrows;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveExplosiveArrow implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp() && sender instanceof Player player) {

            ItemStack arrows = ExplosiveArrow.explosiveArrow(Integer.parseInt(args[0]));
            player.getInventory().addItem(arrows);

        }



        return true;
    }

}
