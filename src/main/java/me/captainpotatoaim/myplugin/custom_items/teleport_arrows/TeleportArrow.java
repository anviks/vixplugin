package me.captainpotatoaim.myplugin.custom_items.teleport_arrows;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportArrow {
    public static ItemStack getItem(int count) {
        ItemStack tpArrow = new ItemStack(Material.ARROW, count);
        ItemMeta tpArrowMeta = tpArrow.getItemMeta();
        assert tpArrowMeta != null;
        tpArrowMeta.addEnchant(Enchantment.LUCK, 1, true);
        tpArrowMeta.setDisplayName(ChatColor.DARK_AQUA + "Teleport arrow");
        tpArrow.setItemMeta(tpArrowMeta);
        CustomItem.setType(tpArrow, TeleportArrow.class);

        return tpArrow;
    }
}
