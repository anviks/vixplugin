package me.captainpotatoaim.myplugin.enderman

import me.captainpotatoaim.myplugin.custom_items.CustomItem.Companion.isOfType
import me.captainpotatoaim.myplugin.custom_items.teleport_arrows.TeleportArrow
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.data.Waterlogged
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import java.util.HashSet
import java.util.Random
import kotlin.math.floor

class ArrowListener : Listener {

    @EventHandler
    fun onArrowHit(event: ProjectileHitEvent) {
        val projectile = event.getEntity()
        if (isOfType(projectile, TeleportArrow::class.java)) return

        val hitEntity = event.hitEntity
        if (hitEntity == null) return

        if (BecomeEnderman.endermenPlayers.contains(hitEntity.uniqueId)) {
            val hitEntityLocation = hitEntity.location
            val availableLocations: MutableSet<Location> = HashSet<Location>()

            // Find a suitable location to teleport the player to
            for (x in -32..32) {
                for (y in -32..32) {
                    innerCoordinateLoop@ for (z in -32..32) {
                        val destination = hitEntityLocation.clone()
                        destination.x = floor(destination.x) + 0.5
                        destination.y = floor(destination.y)
                        destination.z = floor(destination.z) + 0.5
                        destination.add(x.toDouble(), y.toDouble(), z.toDouble())

                        for (i in 0..2) {
                            val block = destination.add(0.0, 1.0, 0.0).block
                            if (!block.isPassable || block.isLiquid) {
                                continue@innerCoordinateLoop
                            }
                        }

                        destination.subtract(0.0, 3.0, 0.0)
                        val block = destination.block
                        if (block.isLiquid || block.isPassable) {
                            continue
                        }

                        val blockData = block.blockData

                        if (blockData is Waterlogged
                            && blockData.isWaterlogged
                        ) {
                            continue
                        }

                        availableLocations.add(destination)
                    }
                }
            }

            val random = Random()
            val destination = availableLocations.stream().toList()[random.nextInt(0, availableLocations.size)]

            val world = hitEntity.world
            world.playSound(hitEntity, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
            world.spawnParticle(Particle.PORTAL, hitEntityLocation, 200, 0.3, 0.3, 0.3)
            hitEntity.teleport(destination.add(0.0, 1.0, 0.0))
            event.isCancelled = true
        }
    }
}
