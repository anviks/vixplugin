package com.github.anviks.vixplugin.custom_items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.Plugin
import org.bukkit.projectiles.BlockProjectileSource

class ExplosiveArrow(private val plugin: Plugin) : CustomItem(), Listener {

    private var dispenserShotExplosiveArrow = false

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

    @EventHandler
    fun onDispenserPowered(event: BlockDispenseEvent) {
        if (isOfType(event.item, ExplosiveArrow::class.java)) {
            dispenserShotExplosiveArrow = true
        }
    }

    @EventHandler
    fun onBowArrowShot(event: EntityShootBowEvent) {
        val consumable = event.consumable
        val projectile = event.projectile

        if (consumable != null && isOfType(consumable, ExplosiveArrow::class.java)) {
            setType(projectile, ExplosiveArrow::class.java)
        }
    }

    @EventHandler
    fun onArrowShot(event: ProjectileLaunchEvent) {
        val projectile = event.entity
        val shooter = projectile.shooter

        if (dispenserShotExplosiveArrow && shooter is BlockProjectileSource) {
            dispenserShotExplosiveArrow = false
            setType(projectile, ExplosiveArrow::class.java)
        }
    }

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        val projectile = event.entity
        if (isOfType(projectile, ExplosiveArrow::class.java)) {
            projectile.world.createExplosion(projectile.location, 7f, false, true, projectile)
            projectile.remove()
        }
    }
}