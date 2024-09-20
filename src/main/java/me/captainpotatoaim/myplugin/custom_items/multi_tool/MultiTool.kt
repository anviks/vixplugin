package me.captainpotatoaim.myplugin.custom_items.multi_tool;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MultiTool extends CustomItem {

    @Override
    public ItemStack getItem(int count) {
        ItemStack tool = new ItemStack(Material.DIAMOND_PICKAXE);
        var meta = tool.getItemMeta();
        assert meta != null;
        meta.displayName(Component.text("Multi-tool"));
        meta.lore(List.of(Component.text("One tool to fit all your needs.")));
        meta.addEnchant(Enchantment.EFFICIENCY, 5, false);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.UNBREAKING, 3, false);
        tool.setItemMeta(meta);
        CustomItem.setType(tool, MultiTool.class);

        return tool;
    }
}
