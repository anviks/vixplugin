package me.captainpotatoaim.myplugin.custom_items.multi_tool;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiTool {
    static final String IDENTIFIER = Tagger.getIdentifier("multi-tool");

    public static @NotNull ItemStack getMultiTool() {
        ItemStack tool = new ItemStack(Material.DIAMOND_PICKAXE);
        var meta = tool.getItemMeta();
        meta.setDisplayName("Multi-tool");
        meta.setLore(List.of("One tool to fit all your needs."));
        meta.addEnchant(Enchantment.DIG_SPEED, 5, false);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        tool.setItemMeta(meta);
        Tagger.tagItem(tool, IDENTIFIER);

        return tool;
    }
}
