package me.captainpotatoaim.myplugin.grappling_hook;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class GrapplingHook {
    static final String IDENTIFIER = Tagger.getIdentifier("grappling-hook");

    static ItemStack getHook() {
        var hook = new ItemStack(Material.FISHING_ROD);
        var meta = hook.getItemMeta();
        meta.setDisplayName("Grappling hook");
        meta.setUnbreakable(true);
        hook.setItemMeta(meta);
        Tagger.tagItem(hook, IDENTIFIER);

        return hook;
    }
}
