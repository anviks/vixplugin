package me.captainpotatoaim.myplugin.custom_items.teleport_arrows

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent

class TeleportArrowLand : Listener {

    @EventHandler
    fun onArrowShot(event: EntityShootBowEvent) {
        if (CustomItem.isOfType(event.consumable!!, TeleportArrow::class.java)) {
            CustomItem.setType(event.projectile, TeleportArrow::class.java)
        }
    }

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        val arrow = event.entity as? Arrow ?: return

        if (CustomItem.isOfType(arrow, TeleportArrow::class.java)) {
            event.isCancelled = true
            val player = checkNotNull(arrow.shooter as Player)
            val direction = player.eyeLocation.direction
            player.teleport(event.entity.location.setDirection(direction))
            event.entity.remove()
        }
    }
}