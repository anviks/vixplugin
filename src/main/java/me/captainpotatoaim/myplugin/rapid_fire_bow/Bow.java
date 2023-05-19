package me.captainpotatoaim.myplugin.rapid_fire_bow;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;

public class Bow {
    static ItemStack getBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        var meta = bow.getItemMeta();
        bow.setItemMeta(meta);

        return bow;
    }
}
