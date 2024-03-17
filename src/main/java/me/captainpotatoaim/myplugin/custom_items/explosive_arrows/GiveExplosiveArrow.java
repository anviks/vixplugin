package me.captainpotatoaim.myplugin.custom_items.explosive_arrows;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveExplosiveArrow implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
//        try {
//            Initializer.dispatcher.execute(command.getName() + " " + String.join(" ", args), sender);
//        } catch (CommandSyntaxException e) {
//            System.out.println(e);
//            Bukkit.broadcastMessage(e.getMessage());
//        }
//
//        new CommandAPICommand("enchantitem")
//                .withArguments(new EnchantmentArgument("enchantment"))
//                .withArguments(new IntegerArgument("level", 1, 5))
//                .executesPlayer((player, args) -> {
//                    Enchantment enchantment = (Enchantment) args[0];
//                    int level = (int) args[1];
//
//                    //Add the enchantment
//                    player.getInventory().getItemInMainHand().addEnchantment(enchantment, level);
//                })
//                .register();

        int amount = 1;

        if (args.length > 0) {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        if (sender.isOp() && sender instanceof Player player) {
            ItemStack arrows = ExplosiveArrow.getExplosiveArrow(amount);
            player.getInventory().addItem(arrows);
        }

        return true;
    }

}
