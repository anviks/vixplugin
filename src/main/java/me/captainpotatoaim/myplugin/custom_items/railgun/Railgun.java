package me.captainpotatoaim.myplugin.custom_items.railgun;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.ChatColor.*;

public class Railgun {
    static final String IDENTIFIER = Tagger.getIdentifier("railgun");

    public static ItemStack getRailgun() {
        ItemStack railGun = new ItemStack(Material.TRIDENT, 1);
        ItemMeta railGunMeta = railGun.getItemMeta();
        railGunMeta.setDisplayName(GRAY + "R" + YELLOW + "A" + GRAY + "I" + YELLOW + "L" + GRAY + "G" + YELLOW + "U" + GRAY + "N");
        railGunMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        railGun.setItemMeta(railGunMeta);
        Tagger.tagItem(railGun, IDENTIFIER);

        return railGun;

    }

}
