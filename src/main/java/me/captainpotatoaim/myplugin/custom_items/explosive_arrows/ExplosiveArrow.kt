package me.captainpotatoaim.myplugin.custom_items.explosive_arrows

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.Plugin

class ExplosiveArrow(private val plugin: Plugin) : CustomItem() {

    override fun getItem(count: Int): ItemStack {
        val arrows = ItemStack(Material.SPECTRAL_ARROW, count)
        val arrowMeta = checkNotNull(arrows.itemMeta)
        arrowMeta.displayName(Component.text("Explosive Arrow", NamedTextColor.YELLOW))
        arrowMeta.addEnchant(Enchantment.INFINITY, 1, false)
        arrows.setItemMeta(arrowMeta)
        setType(arrows, ExplosiveArrow::class.java)

        return arrows
    }

    fun getRecipe(): ShapedRecipe {
        val arrow = getItem(1)
        val key = NamespacedKey(this.plugin, "explosive_arrow")
        val recipe = ShapedRecipe(key, arrow)
        recipe.shape("GGG", "GAG", "GGG")
        recipe.setIngredient('G', Material.GUNPOWDER)
        recipe.setIngredient('A', Material.ARROW)

        return recipe
    }
}