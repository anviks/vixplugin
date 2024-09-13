package me.captainpotatoaim.myplugin.custom_items.multi_tool;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiTool {
    public static @NotNull ItemStack getItem() {
        ItemStack tool = new ItemStack(Material.DIAMOND_PICKAXE);
        var meta = tool.getItemMeta();
        assert meta != null;
        meta.setDisplayName("Multi-tool");
        meta.setLore(List.of("One tool to fit all your needs."));
        meta.addEnchant(Enchantment.DIG_SPEED, 5, false);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        tool.setItemMeta(meta);
        CustomItem.setType(tool, MultiTool.class);

        return tool;
    }
}
