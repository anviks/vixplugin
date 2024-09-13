package me.captainpotatoaim.myplugin.custom_items.grappling_hook;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class GrapplingHook {
    static ItemStack getHook() {
        var hook = new ItemStack(Material.FISHING_ROD);
        var meta = hook.getItemMeta();
        assert meta != null;
        meta.setDisplayName("Grappling hook");
        meta.setUnbreakable(true);
        hook.setItemMeta(meta);
        CustomItem.setType(hook, GrapplingHook.class);

        return hook;
    }
}
