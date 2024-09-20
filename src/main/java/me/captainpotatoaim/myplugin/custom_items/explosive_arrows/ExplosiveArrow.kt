package me.captainpotatoaim.myplugin.custom_items.explosive_arrows;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class ExplosiveArrow extends CustomItem {

    private final Plugin plugin;

    public ExplosiveArrow(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack getItem(int count) {
        ItemStack arrows = new ItemStack(Material.SPECTRAL_ARROW, count);
        ItemMeta arrowMeta = arrows.getItemMeta();
        assert arrowMeta != null;
        arrowMeta.displayName(text("Explosive Arrow", YELLOW));
        arrowMeta.addEnchant(Enchantment.INFINITY, 1, false);
        arrows.setItemMeta(arrowMeta);
        CustomItem.setType(arrows, ExplosiveArrow.class);

        return arrows;
    }

    public ShapedRecipe getRecipe() {
        ItemStack arrow = getItem(1);
        NamespacedKey key = new NamespacedKey(this.plugin, "explosive_arrow");
        ShapedRecipe recipe = new ShapedRecipe(key, arrow);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GUNPOWDER);
        recipe.setIngredient('A', Material.ARROW);

        return recipe;
    }
}
