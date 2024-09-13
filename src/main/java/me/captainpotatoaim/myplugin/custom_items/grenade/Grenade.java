package me.captainpotatoaim.myplugin.custom_items.grenade;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Grenade {
    public static ItemStack getItem(int count) {
        ItemStack grenade = new ItemStack(Material.EXPERIENCE_BOTTLE, count);
        ItemMeta grenadeMeta = grenade.getItemMeta();
        assert grenadeMeta != null;
        grenadeMeta.setDisplayName(ChatColor.RED + "GRENADE");
        grenadeMeta.setLore(List.of("Toss it at someone."));
        grenade.setItemMeta(grenadeMeta);
        CustomItem.setType(grenade, Grenade.class);

        return grenade;
    }
}
