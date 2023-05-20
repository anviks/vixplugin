package me.captainpotatoaim.myplugin.explosive_arrows;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ExplosiveArrow {
    static final byte[] secret = Tagger.generateSecret();

    public static ItemStack getExplosiveArrow(int count) {
        ItemStack arrows = new ItemStack(Material.SPECTRAL_ARROW, count);
        ItemMeta arrowMeta = arrows.getItemMeta();
        arrowMeta.setDisplayName(ChatColor.YELLOW + "Explosive Arrow");
        arrowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        arrows.setItemMeta(arrowMeta);
        Tagger.tagItem(arrows, secret);

        return arrows;
    }

}
