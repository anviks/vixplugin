package me.captainpotatoaim.myplugin.railgun;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RailGunItem {

    public static ItemStack create() {
        ItemStack railGun = new ItemStack(Material.TRIDENT, 1);
        ItemMeta railGunMeta = railGun.getItemMeta();
        railGunMeta.setDisplayName(ChatColor.GRAY + "R" + ChatColor.YELLOW + "A" + ChatColor.GRAY + "I" + ChatColor.YELLOW + "L" + ChatColor.GRAY + "G" + ChatColor.YELLOW + "U" + ChatColor.GRAY + "N");
        railGunMeta.setCustomModelData(61245461);
        railGunMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

        railGun.setItemMeta(railGunMeta);

        return railGun;

    }

}
