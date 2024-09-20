package me.captainpotatoaim.myplugin.custom_items.rapid_fire_bow;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RapidFireBow extends CustomItem {

    @Override
    public ItemStack getItem(int count) {
        ItemStack bow = new ItemStack(Material.BOW);
        var meta = bow.getItemMeta();
        assert meta != null;
        bow.setItemMeta(meta);
        // TODO: Customise item
        CustomItem.setType(bow, RapidFireBow.class);

        return bow;
    }
}
