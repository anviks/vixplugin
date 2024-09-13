package me.captainpotatoaim.myplugin.custom_items.tnt;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomFuseTNT {
    static ItemStack create(int amount, double fuseSeconds) {
        ItemStack item = new ItemStack(Material.TNT, amount);
        var meta = item.getItemMeta();
        assert meta != null;
        meta.setLore(List.of("Fuse time: " + fuseSeconds + " seconds"));
        item.setItemMeta(meta);
        CustomItem.setType(item, CustomFuseTNT.class);

        return item;
    }

    static ItemStack create(double fuseSeconds) {
        return create(1, fuseSeconds);
    }
}
