package me.captainpotatoaim.myplugin.teleport_arrows;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportArrow {
    static final String IDENTIFIER = Tagger.getIdentifier("teleport-arrow");

    public static ItemStack tpArrow(int count) {
        ItemStack tpArrow = new ItemStack(Material.ARROW, count);
        ItemMeta tpArrowMeta = tpArrow.getItemMeta();
        assert tpArrowMeta != null;
        tpArrowMeta.addEnchant(Enchantment.LUCK, 1, true);
        tpArrowMeta.setDisplayName(ChatColor.DARK_AQUA + "Teleport arrow");
        tpArrow.setItemMeta(tpArrowMeta);
        Tagger.tagItem(tpArrow, IDENTIFIER);

        return tpArrow;
    }

}
