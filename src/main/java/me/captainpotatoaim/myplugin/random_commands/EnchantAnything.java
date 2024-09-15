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
        Enchantment enchantment;

        switch (args[0]) {
            // <editor-fold defaultstate="collapsed" desc="enchantments">
            case "aqua_affinity" -> enchantment = Enchantment.AQUA_AFFINITY;
            case "bane_of_arthropods" -> enchantment = Enchantment.BANE_OF_ARTHROPODS;
            case "binding_curse" -> enchantment = Enchantment.BINDING_CURSE;
            case "blast_protection" -> enchantment = Enchantment.BLAST_PROTECTION;
            case "channeling" -> enchantment = Enchantment.CHANNELING;
            case "depth_strider" -> enchantment = Enchantment.DEPTH_STRIDER;
            case "efficiency" -> enchantment = Enchantment.EFFICIENCY;
            case "feather_falling" -> enchantment = Enchantment.FEATHER_FALLING;
            case "fire_aspect" -> enchantment = Enchantment.FIRE_ASPECT;
            case "fire_protection" -> enchantment = Enchantment.FIRE_PROTECTION;
            case "flame" -> enchantment = Enchantment.FLAME;
            case "fortune" -> enchantment = Enchantment.FORTUNE;
            case "frost_walker" -> enchantment = Enchantment.FROST_WALKER;
            case "impaling" -> enchantment = Enchantment.IMPALING;
            case "infinity" -> enchantment = Enchantment.INFINITY;
            case "knockback" -> enchantment = Enchantment.KNOCKBACK;
            case "looting" -> enchantment = Enchantment.LOOTING;
            case "loyalty" -> enchantment = Enchantment.LOYALTY;
            case "luck_of_the_sea" -> enchantment = Enchantment.LUCK_OF_THE_SEA;
            case "lure" -> enchantment = Enchantment.LURE;
            case "mending" -> enchantment = Enchantment.MENDING;
            case "multishot" -> enchantment = Enchantment.MULTISHOT;
            case "piercing" -> enchantment = Enchantment.PIERCING;
            case "power" -> enchantment = Enchantment.POWER;
            case "projectile_protection" -> enchantment = Enchantment.PROJECTILE_PROTECTION;
            case "protection" -> enchantment = Enchantment.PROTECTION;
            case "punch" -> enchantment = Enchantment.PUNCH;
            case "quick_charge" -> enchantment = Enchantment.QUICK_CHARGE;
            case "respiration" -> enchantment = Enchantment.RESPIRATION;
            case "riptide" -> enchantment = Enchantment.RIPTIDE;
            case "sharpness" -> enchantment = Enchantment.SHARPNESS;
            case "silk_touch" -> enchantment = Enchantment.SILK_TOUCH;
            case "smite" -> enchantment = Enchantment.SMITE;
            case "soul_speed" -> enchantment = Enchantment.SOUL_SPEED;
            case "sweeping" -> enchantment = Enchantment.SWEEPING_EDGE;
            case "swift_sneak" -> enchantment = Enchantment.SWIFT_SNEAK;
            case "thorns" -> enchantment = Enchantment.THORNS;
            case "unbreaking" -> enchantment = Enchantment.UNBREAKING;
            case "vanishing_curse" -> enchantment = Enchantment.VANISHING_CURSE;
            default -> throw new IllegalStateException("Unexpected value: " + args[0]);
            // </editor-fold>
        }

        if (sender.isOp()
                && sender instanceof Player player
                && player.getInventory().getItemInMainHand().getItemMeta() != null) {
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            int level;
            try {
                level = args.length > 1
                        ? Integer.parseInt(args[1])
                        : 1;
            } catch (NumberFormatException e) {
                level = 1;
            }
            itemMeta.addEnchant(enchantment, level, true);
            item.setItemMeta(itemMeta);
        } else {
            sender.sendMessage(ChatColor.RED + "fuck you");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
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
                if (!args[0].isEmpty()) {
                    return List.of("1", "2", "3");
                }
                return List.of();
            }

            default -> {
                return List.of();
            }
        }
    }
}
