package me.captainpotatoaim.myplugin.custom_items

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

class TeleportArrow : CustomItem(), Listener {

    override fun getItem(count: Int): ItemStack {
        val tpArrow = ItemStack(Material.ARROW, count)
        val tpArrowMeta = checkNotNull(tpArrow.itemMeta)
        tpArrowMeta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true)
        tpArrowMeta.displayName(Component.text("Teleport arrow", NamedTextColor.DARK_AQUA))
        tpArrow.setItemMeta(tpArrowMeta)
        setType(tpArrow, TeleportArrow::class.java)

        return tpArrow
    }

    @EventHandler
    fun onArrowShot(event: EntityShootBowEvent) {
        if (isOfType(event.consumable!!, TeleportArrow::class.java)) {
            setType(event.projectile, TeleportArrow::class.java)
        }
    }

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        val arrow = event.entity as? Arrow ?: return

        if (isOfType(arrow, TeleportArrow::class.java)) {
            event.isCancelled = true
            val player = checkNotNull(arrow.shooter as Player)
            val direction = player.eyeLocation.direction
            player.teleport(event.entity.location.setDirection(direction))
            event.entity.remove()
        }
    }
}