package me.captainpotatoaim.myplugin.explosive_arrows;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveExplosiveArrow implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && sender instanceof Player player) {
            ItemStack arrows = ExplosiveArrow.getExplosiveArrow(Integer.parseInt(args[0]));
            player.getInventory().addItem(arrows);
        }

        return true;
    }

}
