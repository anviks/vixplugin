package me.captainpotatoaim.myplugin.custom_items.teleport_arrows;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;

public class TeleportArrow extends CustomItem {

    @Override
    public ItemStack getItem(int count) {
        ItemStack tpArrow = new ItemStack(Material.ARROW, count);
        ItemMeta tpArrowMeta = tpArrow.getItemMeta();
        assert tpArrowMeta != null;
        tpArrowMeta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        tpArrowMeta.displayName(text("Teleport arrow", DARK_AQUA));
        tpArrow.setItemMeta(tpArrowMeta);
        CustomItem.setType(tpArrow, TeleportArrow.class);

        return tpArrow;
    }
}
