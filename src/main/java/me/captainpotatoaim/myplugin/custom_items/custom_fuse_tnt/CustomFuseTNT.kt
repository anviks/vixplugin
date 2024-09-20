package me.captainpotatoaim.myplugin.custom_items.custom_fuse_tnt;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import me.captainpotatoaim.myplugin.util.PDCManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CustomFuseTNT extends CustomItem {

    public static ItemStack getItem(int count, float fuseSeconds) {
        ItemStack item = new ItemStack(Material.TNT, count);
        var meta = item.getItemMeta();
        assert meta != null;
        meta.lore(List.of(Component.text("Fuse time: " + fuseSeconds + " seconds")));
        item.setItemMeta(meta);
        CustomItem.setType(item, CustomFuseTNT.class);
        PDCManager.setData(item, "fuse_seconds", PersistentDataType.FLOAT, fuseSeconds);

        return item;
    }

    @Override
    public ItemStack getItem(int count) {
        return getItem(count, 8);
    }
}
