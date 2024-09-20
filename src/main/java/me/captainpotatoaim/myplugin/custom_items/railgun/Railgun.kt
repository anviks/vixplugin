package me.captainpotatoaim.myplugin.custom_items.railgun;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import me.captainpotatoaim.myplugin.util.AdventureHelper;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class Railgun extends CustomItem {

    @Override
    public ItemStack getItem(int count) {
        ItemStack railGun = new ItemStack(Material.TRIDENT, 1);
        ItemMeta railGunMeta = railGun.getItemMeta();
        assert railGunMeta != null;
        railGunMeta.displayName(AdventureHelper.createAlternatingColoredText("RAILGUN", GRAY, YELLOW));
        railGunMeta.addEnchant(Enchantment.INFINITY, 1, true);
        railGun.setItemMeta(railGunMeta);
        CustomItem.setType(railGun, Railgun.class);

        return railGun;
    }
}
