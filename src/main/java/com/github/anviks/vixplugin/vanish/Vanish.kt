package com.github.anviks.vixplugin.vanish

import com.github.anviks.vixplugin.CustomCommand
import com.github.anviks.vixplugin.util.getPDCData
import com.github.anviks.vixplugin.util.setPDCData
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.kyori.adventure.text.format.TextDecoration.BOLD
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

class Vanish(private val plugin: JavaPlugin) : CustomCommand, Listener {

    private val vanishingPlayers = mutableSetOf<UUID>()
    private val alreadyVanishingMessage =
        text("Wait for (un)vanishing process to finish before running the command again.", RED, BOLD)

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (isPlayerVanished(player)) {
            this.updatePlayerVisibilityForAll(player, false)
            this.displayVanishedActionBar(player)
        }

        for (onlinePlayer in Bukkit.getOnlinePlayers().minus(player)) {
            if (isPlayerVanished(onlinePlayer)) {
                this.updatePlayerVisibility(onlinePlayer, player, false)
            }
        }
    }

    override fun register() {
        val commandBase = CommandAPICommand("vanish")
            .withPermission(CommandPermission.OP)
            .withRequirement { it is Player }

        commandBase.copy()
            .withArguments(LiteralArgument("silently"))
            .executesPlayer(this::runSilently)
            .register(plugin)

        commandBase
            .withArguments(LiteralArgument("with-effects"))
            .executesPlayer(this::runWithEffects)
            .register(plugin)
    }

    private fun runSilently(player: Player, args: CommandArguments) {
        if (player.uniqueId in vanishingPlayers) {
            player.sendMessage(alreadyVanishingMessage)
            return
        }

        if (isPlayerVanished(player)) {
            this.setPlayerVisibility(player, true)
            this.updatePlayerVisibilityForAll(player, true)
            this.broadcastJoinEvent(player)
            this.showVisibilityCountdown(player, 0)
        } else {
            this.broadcastQuitEvent(player)
            this.setPlayerVisibility(player, false)
            this.updatePlayerVisibilityForAll(player, false)
            this.displayVanishedActionBar(player)
        }
    }

    private fun runWithEffects(player: Player, args: CommandArguments) {
        if (player.uniqueId in vanishingPlayers) {
            player.sendMessage(alreadyVanishingMessage)
            return
        }

        vanishingPlayers.add(player.uniqueId)

        if (isPlayerVanished(player)) {
            val effectsDuration = this.spawnUnvanishSpecialEffects(player)
            this.showVisibilityCountdown(player, effectsDuration)

            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                vanishingPlayers.remove(player.uniqueId)
                // Get the player object again in case the player logged out and back in during the delay
                val player = Bukkit.getPlayer(player.uniqueId) ?: return@Runnable
                this.setPlayerVisibility(player, true)
                this.updatePlayerVisibilityForAll(player, true)
                this.broadcastJoinEvent(player)
            }, effectsDuration)
        } else {
            this.broadcastQuitEvent(player)
            this.setPlayerVisibility(player, false)
            this.updatePlayerVisibilityForAll(player, false)
            this.spawnVanishSpecialEffects(player)
            this.displayVanishedActionBar(player)

            vanishingPlayers.remove(player.uniqueId)
        }
    }

    private fun spawnUnvanishSpecialEffects(player: Player): Long {
        val neighbourOffsets = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to 1, 1 to 1, 1 to 0, 1 to -1, 0 to -1)
        val scheduler = Bukkit.getScheduler()
        var delay = 0L

        for (offset in neighbourOffsets) {
            scheduler.runTaskLater(
                plugin,
                Runnable { if (player.isOnline) spawnLightningWithOffset(player.location, offset) },
                delay
            )
            delay += 5
        }

        delay += 20

        scheduler.runTaskLater(plugin, Runnable {
            // Get the player object again in case the player logged out and back in during the delay
            val player = Bukkit.getPlayer(player.uniqueId) ?: return@Runnable
            for (offset in neighbourOffsets) {
                spawnLightningWithOffset(player.location, offset)
            }
        }, delay)

        return delay
    }

    private fun showVisibilityCountdown(player: Player, fromTicks: Long) {
        for (i in ceil(fromTicks.toDouble() / 20).toLong() downTo 0) {
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                val message = if (i != 0L) "Visible in $i" else "You are now visible"
                player.sendActionBar(text(message, NamedTextColor.GREEN))
            }, max(fromTicks - i * 20, 0))
        }
    }

    private fun isPlayerVanished(player: Player): Boolean {
        return player.getPDCData("vanished", PersistentDataType.BOOLEAN) == true
    }

    private fun setPlayerVisibility(player: Player, visible: Boolean) {
        player.setPDCData("vanished", PersistentDataType.BOOLEAN, !visible)
    }

    private fun displayVanishedActionBar(player: Player) {
        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline || !isPlayerVanished(player) || player.uniqueId in vanishingPlayers) {
                    cancel()
                    return
                }
                val message = "You are vanished"
                val actionBarMessage = text(message, NamedTextColor.GREEN)
                player.sendActionBar(actionBarMessage)
            }
        }.runTaskTimer(plugin, 0, 40)
    }

    private fun spawnLightningWithOffset(location: Location, offset: Pair<Int, Int>) {
        location.y -= 1
        location.x += offset.first
        location.z += offset.second
        location.world.strikeLightningEffect(location)
    }

    private fun spawnVanishSpecialEffects(player: Player) {
        val location = player.location
        location.y += 1
        player.world.spawnParticle(Particle.LARGE_SMOKE, location, 250, 0.5, 0.5, 0.5, 0.1)
    }

    private fun broadcastJoinEvent(player: Player) {
        val joinEvent = PlayerJoinEvent(
            player,
            player.displayName().append(text(" joined the game")).color(NamedTextColor.YELLOW)
        )
        Bukkit.getPluginManager().callEvent(joinEvent)
        joinEvent.joinMessage()?.let { Bukkit.broadcast(it) }
    }

    private fun broadcastQuitEvent(player: Player) {
        val quitEvent = PlayerQuitEvent(
            player,
            player.displayName().append(text(" left the game")).color(NamedTextColor.YELLOW),
            PlayerQuitEvent.QuitReason.DISCONNECTED
        )
        Bukkit.getPluginManager().callEvent(quitEvent)
        quitEvent.quitMessage()?.let { Bukkit.broadcast(it) }
    }

    private fun updatePlayerVisibilityForAll(player: Player, visible: Boolean) {
        for (onlinePlayer in Bukkit.getOnlinePlayers().minus(player)) {
            updatePlayerVisibility(player, onlinePlayer, visible)
        }
    }

    private fun updatePlayerVisibility(target: Player, observer: Player, visible: Boolean) {
        val packet = if (visible) getTabListAddPacket(target) else getTabListRemovePacket(target)

        val nmsObserver = (observer as CraftPlayer).handle
        nmsObserver.connection.sendPacket(packet)

        if (visible) {
            observer.showPlayer(plugin, target)
        } else {
            observer.hidePlayer(plugin, target)
        }
    }

    private fun getTabListRemovePacket(player: Player): ClientboundPlayerInfoRemovePacket {
        return ClientboundPlayerInfoRemovePacket(listOf(player.uniqueId))
    }

    private fun getTabListAddPacket(player: Player): ClientboundPlayerInfoUpdatePacket {
        val nmsPlayer = (player as CraftPlayer).handle

        val actions = EnumSet.of(
            ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
            ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED
        )

        return ClientboundPlayerInfoUpdatePacket(actions, listOf(nmsPlayer))
    }
}