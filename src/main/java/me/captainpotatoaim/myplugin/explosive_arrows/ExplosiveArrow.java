package me.captainpotatoaim.myplugin.explosive_arrows;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ExplosiveArrow {

    public static ItemStack explosiveArrow(int count) {

        ItemStack arrows = new ItemStack(Material.SPECTRAL_ARROW, count);
        ItemMeta arrowMeta = arrows.getItemMeta();
        assert arrowMeta != null;
        arrowMeta.setDisplayName(ChatColor.YELLOW + "Explosive Arrow");
        arrowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        arrows.setItemMeta(arrowMeta);
        return arrows;

    }

}
