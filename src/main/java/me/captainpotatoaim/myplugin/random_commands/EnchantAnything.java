package me.captainpotatoaim.myplugin.random_commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnchantAnything implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Enchantment enchantment = null;

        switch (args[0]) {
            case "aqua_affinity" -> enchantment = Enchantment.WATER_WORKER;
            case "bane_of_arthropods" -> enchantment = Enchantment.DAMAGE_ARTHROPODS;
            case "binding_curse" -> enchantment = Enchantment.BINDING_CURSE;
            case "blast_protection" -> enchantment = Enchantment.PROTECTION_EXPLOSIONS;
            case "channeling" -> enchantment = Enchantment.CHANNELING;
            case "depth_strider" -> enchantment = Enchantment.DEPTH_STRIDER;
            case "efficiency" -> enchantment = Enchantment.DIG_SPEED;
            case "feather_falling" -> enchantment = Enchantment.PROTECTION_FALL;
            case "fire_aspect" -> enchantment = Enchantment.FIRE_ASPECT;
            case "fire_protection" -> enchantment = Enchantment.PROTECTION_FIRE;
            case "flame" -> enchantment = Enchantment.ARROW_FIRE;
            case "fortune" -> enchantment = Enchantment.LOOT_BONUS_BLOCKS;
            case "frost_walker" -> enchantment = Enchantment.FROST_WALKER;
            case "impaling" -> enchantment = Enchantment.IMPALING;
            case "infinity" -> enchantment = Enchantment.ARROW_INFINITE;
            case "knockback" -> enchantment = Enchantment.KNOCKBACK;
            case "looting" -> enchantment = Enchantment.LOOT_BONUS_MOBS;
            case "loyalty" -> enchantment = Enchantment.LOYALTY;
            case "luck_of_the_sea" -> enchantment = Enchantment.LUCK;
            case "lure" -> enchantment = Enchantment.LURE;
            case "mending" -> enchantment = Enchantment.MENDING;
            case "multishot" -> enchantment = Enchantment.MULTISHOT;
            case "piercing" -> enchantment = Enchantment.PIERCING;
            case "power" -> enchantment = Enchantment.ARROW_DAMAGE;
            case "projectile_protection" -> enchantment = Enchantment.PROTECTION_PROJECTILE;
            case "protection" -> enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
            case "punch" -> enchantment = Enchantment.ARROW_KNOCKBACK;
            case "quick_charge" -> enchantment = Enchantment.QUICK_CHARGE;
            case "respiration" -> enchantment = Enchantment.OXYGEN;
            case "riptide" -> enchantment = Enchantment.RIPTIDE;
            case "sharpness" -> enchantment = Enchantment.DAMAGE_ALL;
            case "silk_touch" -> enchantment = Enchantment.SILK_TOUCH;
            case "smite" -> enchantment = Enchantment.DAMAGE_UNDEAD;
            case "soul_speed" -> enchantment = Enchantment.SOUL_SPEED;
            case "sweeping" -> enchantment = Enchantment.SWEEPING_EDGE;
            case "swift_sneak" -> enchantment = Enchantment.SWIFT_SNEAK;
            case "thorns" -> enchantment = Enchantment.THORNS;
            case "unbreaking" -> enchantment = Enchantment.DURABILITY;
            case "vanishing_curse" -> enchantment = Enchantment.VANISHING_CURSE;
        }

        if (sender.isOp() &&
                sender instanceof Player player &&
                enchantment != null &&
                player.getInventory().getItemInMainHand().getItemMeta() != null) {

            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addEnchant(enchantment, Integer.parseInt(args[1]), true);
            item.setItemMeta(itemMeta);

        } else {
            sender.sendMessage(ChatColor.RED + "fuck you");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        switch (args.length) {

            case 1 -> {
                return List.of("aqua_affinity", "bane_of_arthropods", "binding_curse",
                        "blast_protection", "channeling", "depth_strider", "efficiency",
                        "feather_falling", "fire_aspect", "fire_protection", "flame",
                        "fortune", "frost_walker", "impaling", "infinity", "knockback",
                        "looting", "loyalty", "luck_of_the_sea", "lure", "mending",
                        "multishot", "piercing", "power", "projectile_protection",
                        "protection", "punch", "quick_charge", "respiration", "riptide",
                        "sharpness", "silk_touch", "smite", "soul_speed", "sweeping",
                        "swift_sneak", "thorns", "unbreaking", "vanishing_curse");
            }

            case 2 -> {
                return List.of("1", "2", "3");
            }

            default -> {
                return List.of();
            }

        }

    }

}
