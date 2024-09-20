package me.captainpotatoaim.myplugin.custom_items.explosive_arrows

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.projectiles.BlockProjectileSource

class ExplosiveArrowLand : Listener {

    private var dispenserShotExplosiveArrow = false

    @EventHandler
    fun onDispenserPowered(event: BlockDispenseEvent) {
        if (CustomItem.isOfType(event.item, ExplosiveArrow::class.java)) {
            dispenserShotExplosiveArrow = true
        }
    }

    @EventHandler
    fun onBowArrowShot(event: EntityShootBowEvent) {
        val consumable = event.consumable
        val projectile = event.projectile

        if (consumable != null && CustomItem.isOfType(consumable, ExplosiveArrow::class.java)) {
            CustomItem.setType(projectile, ExplosiveArrow::class.java)
        }
    }

    @EventHandler
    fun onArrowShot(event: ProjectileLaunchEvent) {
        val projectile = event.entity
        val shooter = projectile.shooter

        if (dispenserShotExplosiveArrow && shooter is BlockProjectileSource) {
            dispenserShotExplosiveArrow = false
            CustomItem.setType(projectile, ExplosiveArrow::class.java)
        }
    }

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        val projectile = event.entity
        if (CustomItem.isOfType(projectile, ExplosiveArrow::class.java)) {
            projectile.world.createExplosion(projectile.location, 7f, false, true, projectile)
            projectile.remove()
        }
    }
}