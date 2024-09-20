package me.captainpotatoaim.myplugin.custom_items.railgun

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.EntityEquipment
import org.bukkit.util.Vector

class RailgunListener : Listener {

    @EventHandler
    fun onTridentThrown(event: ProjectileLaunchEvent) {
        if (event.entityType != EntityType.TRIDENT) return
        val shooter = event.entity.shooter as? LivingEntity ?: return
        val equipment: EntityEquipment = shooter.equipment ?: return

        val itemInMainHand = equipment.itemInMainHand
        val itemInOffHand = equipment.itemInOffHand

        val shotTrident =
            if (itemInMainHand.type == Material.TRIDENT)
                itemInMainHand
            else
                itemInOffHand

        if (!CustomItem.isOfType(shotTrident, Railgun::class.java)) {
            return
        }

        event.isCancelled = true
        val shot: Vector = shooter.eyeLocation.direction
        var explosion: Location = shooter.location

        explosion = explosion.add(shot.multiply(1.8)).add(0.0, 2.0, 0.0)
        for (i in 0..<150) {
            explosion = explosion.add(shot)
            shooter.world.createExplosion(explosion, 2.6f, true, true, shooter)
        }
    }
}