package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.PluginState
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.arguments.TimeArgument
import dev.jorel.commandapi.executors.CommandArguments
import kotlinx.datetime.Clock
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.ENDER_PEARL
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class FreezeCommand(
    private val plugin: Plugin,
    private val pluginState: PluginState,
) : CustomCommand, Listener {

    private val unfreezeTasks = mutableMapOf<UUID, BukkitTask>()

    init {
        pluginState.frozenPlayers.forEach {
            val unfreezeAfter = (it.value - Clock.System.now()).inWholeSeconds * 20
            scheduleUnfreeze(it.key, unfreezeAfter)
        }
    }

    override fun getCommands(): List<CommandAPICommand> {
        val freezeCommand = CommandAPICommand("freeze")
            .withPermission("vixplugin.commands.moderator")
            .withArguments(EntitySelectorArgument.ManyPlayers("targets"))
            .withArguments(TimeArgument("duration"))
            .executes(::freezePlayer)

        val unfreezeCommand = CommandAPICommand("unfreeze")
            .withPermission("vixplugin.commands.moderator")
            .withArguments(EntitySelectorArgument.ManyPlayers("targets"))
            .executes(::unfreezePlayer)

        return listOf(freezeCommand, unfreezeCommand)
    }

    private fun freezePlayer(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!
        val durationTicks = (arguments.get("duration") as Int).coerceIn(0, Int.MAX_VALUE / 2)
        val duration = (durationTicks / 20).seconds
        val unfreezeAt = Clock.System.now().plus(duration)

        for (target in targets) {
            sender.sendMessage(
                target
                    .displayName()
                    .append(text(" has been frozen for $duration."))
                    .color(AQUA)
                    .decorate(BOLD)
            )
            target.sendMessage(text("You have been frozen for $duration.", AQUA, BOLD))

            pluginState.frozenPlayers[target.uniqueId] = unfreezeAt
            applyEffects(target, durationTicks)
            scheduleUnfreeze(target.uniqueId, durationTicks.toLong())
        }
    }

    private fun unfreezePlayer(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!

        for (target in targets) {
            applyEffects(target, 0)
            scheduleUnfreeze(target.uniqueId, 0)
            target.freezeTicks = 100  // For a smooth transition
        }
    }

    private fun scheduleUnfreeze(playerUUID: UUID, afterTicks: Long) {
        val task = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            pluginState.frozenPlayers.remove(playerUUID)
            unfreezeTasks.remove(playerUUID)
            val player = plugin.server.getPlayer(playerUUID)
            player?.sendMessage(text("You are no longer frozen.", GREEN, BOLD))
        }, afterTicks)

        unfreezeTasks[playerUUID]?.cancel()
        unfreezeTasks[playerUUID] = task
    }

    private fun applyEffects(target: Player, durationTicks: Int) {
        target.removePotionEffect(PotionEffectType.SLOWNESS)
        target.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 0))
        target.freezeTicks = durationTicks * 2
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.player.uniqueId in pluginState.frozenPlayers && event.hasChangedPosition()) {
            event.isCancelled = true

            val newLocation = event.from
            newLocation.yaw = event.to.yaw
            newLocation.pitch = event.to.pitch
            event.player.teleport(newLocation)
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        if (event.player.uniqueId in pluginState.frozenPlayers
            && event.cause in setOf(CHORUS_FRUIT, ENDER_PEARL)
        ) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        reapplyEffects(event.player)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        reapplyEffects(event.player)
    }

    private fun reapplyEffects(player: Player) {
        val unFreezeAt = pluginState.frozenPlayers[player.uniqueId] ?: return
        val frozenFor = unFreezeAt.minus(Clock.System.now()).toInt(DurationUnit.SECONDS)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            applyEffects(player, frozenFor * 20)
        })
    }
}
