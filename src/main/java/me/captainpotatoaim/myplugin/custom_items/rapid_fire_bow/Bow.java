package me.captainpotatoaim.myplugin.custom_items.rapid_fire_bow;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Bow {
    static final String IDENTIFIER = Tagger.getIdentifier("rapid-fire-bow");

    static ItemStack getBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        var meta = bow.getItemMeta();
        bow.setItemMeta(meta);
        // TODO: Customise item
        Tagger.tagItem(bow, IDENTIFIER);

        return bow;
    }
}
