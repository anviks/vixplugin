package me.captainpotatoaim.myplugin.rapid_fire_bow;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Bow {
    static final String IDENTIFIER = Tagger.getIdentifier("rapid-fire-bow");

    static ItemStack getBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        var meta = bow.getItemMeta();
        bow.setItemMeta(meta);
        Tagger.tagItem(bow, IDENTIFIER);

        return bow;
    }
}
