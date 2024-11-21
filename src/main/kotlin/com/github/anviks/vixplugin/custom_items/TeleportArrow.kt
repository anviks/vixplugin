package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.util.isOfCustomType
import com.github.anviks.vixplugin.util.setCustomType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

class TeleportArrow : CustomItem, Listener {

    override fun getItem(count: Int): ItemStack {
        val tpArrow = ItemStack(Material.ARROW, count)
        val tpArrowMeta = checkNotNull(tpArrow.itemMeta)
        tpArrowMeta.addEnchant(Enchantment.LUCK, 1, true)
        tpArrowMeta.displayName(Component.text("Teleport arrow", NamedTextColor.DARK_AQUA))
        tpArrow.setItemMeta(tpArrowMeta)
        tpArrow.setCustomType<TeleportArrow>()

        return tpArrow
    }

    @EventHandler
    fun onArrowShot(event: EntityShootBowEvent) {
        if (event.consumable!!.isOfCustomType<TeleportArrow>()) {
            event.projectile.setCustomType<TeleportArrow>()
        }
    }

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        val arrow = event.entity as? Arrow ?: return

        if (arrow.isOfCustomType<TeleportArrow>()) {
            event.isCancelled = true
            val player = checkNotNull(arrow.shooter as Player)
            val direction = player.eyeLocation.direction
            player.teleport(event.entity.location.setDirection(direction))
            event.entity.remove()
        }
    }
}