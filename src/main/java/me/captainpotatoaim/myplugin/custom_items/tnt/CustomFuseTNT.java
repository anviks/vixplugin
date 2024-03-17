package me.captainpotatoaim.myplugin.custom_items.tnt;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomFuseTNT {
    static String IDENTIFIER = Tagger.getIdentifier("custom-fuse-tnt");

    static ItemStack create(int amount, double fuseSeconds) {
        ItemStack item = new ItemStack(Material.TNT, amount);
        var meta = item.getItemMeta();
        meta.setLore(List.of("Fuse time: " + fuseSeconds + " seconds"));
        item.setItemMeta(meta);
        Tagger.tagItem(item, IDENTIFIER);

        return item;
    }

    static ItemStack create(double fuseSeconds) {
        return create(1, fuseSeconds);
    }
}
