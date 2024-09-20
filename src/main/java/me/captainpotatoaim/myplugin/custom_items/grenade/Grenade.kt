package me.captainpotatoaim.myplugin.custom_items.grenade;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class Grenade extends CustomItem {

    @Override
    public ItemStack getItem(int count) {
        ItemStack grenade = new ItemStack(Material.EXPERIENCE_BOTTLE, count);
        ItemMeta grenadeMeta = grenade.getItemMeta();
        assert grenadeMeta != null;
        grenadeMeta.displayName(text("GRENADE", RED));
        grenadeMeta.lore(List.of(text("Toss it at someone.")));
        grenade.setItemMeta(grenadeMeta);
        CustomItem.setType(grenade, Grenade.class);

        return grenade;
    }
}
