package me.captainpotatoaim.myplugin.grappling_hook;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class GrapplingHook {
    static final byte[] secret = Tagger.generateSecret();

    static ItemStack getHook() {
        var hook = new ItemStack(Material.FISHING_ROD);
        var meta = hook.getItemMeta();
        meta.setDisplayName("Grappling hook");
        meta.setUnbreakable(true);
        hook.setItemMeta(meta);
        Tagger.tagItem(hook, secret);

        return hook;
    }
}
