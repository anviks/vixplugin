package me.captainpotatoaim.myplugin.custom_items.railgun;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.ChatColor.*;

public class Railgun {
    public static ItemStack getItem() {
        ItemStack railGun = new ItemStack(Material.TRIDENT, 1);
        ItemMeta railGunMeta = railGun.getItemMeta();
        assert railGunMeta != null;
        railGunMeta.setDisplayName(GRAY + "R" + YELLOW + "A" + GRAY + "I" + YELLOW + "L" + GRAY + "G" + YELLOW + "U" + GRAY + "N");
        railGunMeta.addEnchant(Enchantment.INFINITY, 1, true);
        railGun.setItemMeta(railGunMeta);
        CustomItem.setType(railGun, Railgun.class);

        return railGun;
    }
}
