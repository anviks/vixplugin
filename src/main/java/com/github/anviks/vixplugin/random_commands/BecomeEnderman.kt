package com.github.anviks.vixplugin.random_commands

import com.github.anviks.vixplugin.CustomCommand
import com.github.anviks.vixplugin.custom_items.TeleportArrow
import com.github.anviks.vixplugin.util.isOfCustomType
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.ProxyCommandExecutor
import io.papermc.paper.event.entity.EntityMoveEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.data.Waterlogged
import org.bukkit.command.ProxiedCommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import kotlin.math.floor
import kotlin.random.Random


class BecomeEnderman(private val plugin: JavaPlugin) : CustomCommand, Listener {

    private val endermenEntities = mutableSetOf<UUID>()

    override fun register() {
        CommandAPICommand("become-enderman")
            .withPermission(CommandPermission.OP)
            .executesPlayer(this::run)
            .executesProxy(ProxyCommandExecutor(this::runProxy))
            .register(plugin)
    }

    private fun run(sender: Player, args: CommandArguments) {
        if (endermenEntities.remove(sender.uniqueId)) {
            sender.sendMessage(text("You are no longer an enderman", RED))
        } else {
            endermenEntities.add(sender.uniqueId)
            sender.sendMessage(text("You are now an enderman", GREEN))
        }
    }

    private fun runProxy(sender: ProxiedCommandSender, args: CommandArguments) {
        val target = sender.callee as? Entity ?: return

        if (endermenEntities.remove(target.uniqueId)) {
            sender.caller.sendMessage(text("${target.name} is no longer an enderman", RED))
            target.sendMessage(text("You are no longer an enderman", RED))
        } else {
            endermenEntities.add(target.uniqueId)
            sender.caller.sendMessage(text("${target.name} is now an enderman", GREEN))
            target.sendMessage(text("You are now an enderman", GREEN))
        }
    }

    @EventHandler
    fun onArrowHit(event: ProjectileHitEvent) {
        val projectile = event.getEntity()
        if (projectile.isOfCustomType(TeleportArrow::class.java)) return
        val hitEntity = event.hitEntity ?: return

        if (!endermenEntities.contains(hitEntity.uniqueId)) return

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

                    if (blockData is Waterlogged && blockData.isWaterlogged) {
                        continue
                    }

                    availableLocations.add(destination)
                }
            }
        }

        val destination = availableLocations.toList()[Random.nextInt(availableLocations.size)]

        val world = hitEntity.world
        world.playSound(hitEntity, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        world.spawnParticle(Particle.PORTAL, hitEntityLocation, 200, 0.3, 0.3, 0.3)
        hitEntity.teleport(destination.add(0.0, 1.0, 0.0))
        event.isCancelled = true
    }
}
