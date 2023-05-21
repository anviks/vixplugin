package me.captainpotatoaim.myplugin.grenade;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GrenadeItem {
    static final String IDENTIFIER = Tagger.getIdentifier("grenade");

    public static ItemStack grenadeItem(int count) {
        ItemStack grenade = new ItemStack(Material.EXPERIENCE_BOTTLE, count);
        ItemMeta grenadeMeta = grenade.getItemMeta();
        assert grenadeMeta != null;
        grenadeMeta.setDisplayName(ChatColor.RED + "GRENADE");
        grenadeMeta.setLore(List.of("Toss it at someone."));
        grenade.setItemMeta(grenadeMeta);
        Tagger.tagItem(grenade, IDENTIFIER);

        return grenade;
    }
}
