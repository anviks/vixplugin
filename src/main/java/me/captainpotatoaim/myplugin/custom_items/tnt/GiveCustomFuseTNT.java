package me.captainpotatoaim.myplugin.custom_items.tnt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveCustomFuseTNT implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender.isOp() && sender instanceof Player player)) {
            return true;
        }

        int amount = -1;
        double seconds;

        switch (args.length) {
            case 2:
                amount = Integer.parseInt(args[1]);
            case 1:
                seconds = Double.parseDouble(args[0]);
                break;
            default:
                player.sendMessage("Invalid number of arguments.");
                return false;
        }

        if (seconds < 0) {
            seconds = 0;
        }

        seconds = Math.round(seconds * 20) / 20.0;
        ItemStack itemStack = amount > -1 ? CustomFuseTNT.create(amount, seconds) : CustomFuseTNT.create(seconds);
        player.getInventory().addItem(itemStack);

        return true;
    }
}
