package me.captainpotatoaim.myplugin.custom_items.grappling_hook;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GrapplingHook extends CustomItem {

    @Override
    public ItemStack getItem(int count) {
        var hook = new ItemStack(Material.FISHING_ROD, count);
        var meta = hook.getItemMeta();
        assert meta != null;
        meta.displayName(Component.text("Grappling hook"));
        meta.setUnbreakable(true);
        hook.setItemMeta(meta);
        CustomItem.setType(hook, GrapplingHook.class);

        return hook;
    }
}
