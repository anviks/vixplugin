package me.captainpotatoaim.myplugin.custom_items.explosive_arrows;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplosiveArrow {
    static final String identifier = Tagger.getIdentifier("explosive-arrow");

    public static ItemStack getExplosiveArrow(int count) {
        ItemStack arrows = new ItemStack(Material.SPECTRAL_ARROW, count);
        ItemMeta arrowMeta = arrows.getItemMeta();
        arrowMeta.setDisplayName(ChatColor.YELLOW + "Explosive Arrow");
        arrowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        arrows.setItemMeta(arrowMeta);
        Tagger.tagItem(arrows, identifier);

        return arrows;
    }

    public static ShapedRecipe getRecipe() {
        ItemStack arrow = getExplosiveArrow(1);
        NamespacedKey key = new NamespacedKey(Initializer.plugin, "explosive-arrow");
        ShapedRecipe recipe = new ShapedRecipe(key, arrow);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GUNPOWDER);
        recipe.setIngredient('A', Material.ARROW);

        return recipe;
    }
}
