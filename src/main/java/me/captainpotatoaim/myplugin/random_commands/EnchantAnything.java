package me.captainpotatoaim.myplugin.random_commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EnchantmentArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class EnchantAnything {

    public void registerCommand() {
        new CommandAPICommand("enchantanything")
                .withPermission(CommandPermission.OP)
                .withArguments(new EntitySelectorArgument.ManyPlayers("players"))
                .withArguments(new EnchantmentArgument("enchantment"))
                .withArguments(new IntegerArgument("level", 0, 255))
                .executes((sender, args) -> {
                    var players = args.<Collection<Player>>getUnchecked("players");
                    var enchantment = args.getByClass("enchantment", Enchantment.class);
                    var level = args.getByClass("level", Integer.class);

                    assert players != null;
                    assert enchantment != null;
                    assert level != null;

                    for (var player : players) {
                        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                        ItemMeta itemMeta = itemInMainHand.getItemMeta();
                        if (itemMeta == null) break;
                        itemMeta.addEnchant(enchantment, level, true);
                        itemInMainHand.setItemMeta(itemMeta);
                    }
                })
                .register(Initializer.getPlugin());
    }
}
