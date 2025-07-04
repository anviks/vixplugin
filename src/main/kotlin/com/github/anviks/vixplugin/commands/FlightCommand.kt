package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.util.sendBulkToggleMessage
import com.github.anviks.vixplugin.util.isAllowedToFly
import com.github.anviks.vixplugin.util.setAllowedToFly
import com.github.anviks.vixplugin.util.withOverloads
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.java.JavaPlugin

class FlightCommand(private val plugin: JavaPlugin) : CustomCommand, Listener {

    override fun register() {
        CommandAPICommand("fly")
            .withPermission("vixplugin.commands.admin")
            .withOverloads(
                {
                    it.withArguments(EntitySelectorArgument.ManyPlayers("targets"))
                        .executes(::run)
                        .register(plugin)
                },
                {
                    it.executesPlayer(::run)
                        .register(plugin)
                }
            )
    }

    private fun run(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets") ?: listOf(sender as Player)
        val enable = targets.any { !it.isAllowedToFly() }

        val targetMsg: String
        val senderMsg: String
        val color: NamedTextColor

        if (enable) {
            targetMsg = "Flying enabled."
            senderMsg = "Enabled flying for "
            color = GREEN
        } else {
            targetMsg = "Flying disabled."
            senderMsg = "Disabled flying for "
            color = RED
        }

        for (target in targets) {
            target.setAllowedToFly(enable)
            target.allowFlight = enable
            target.isFlying = enable
            target.sendMessage(text(targetMsg, color))
        }

        sendBulkToggleMessage(targets, sender, senderMsg, color)
    }

    @EventHandler
    fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        if (event.newGameMode in setOf(GameMode.SURVIVAL, GameMode.ADVENTURE)) {
            preserveFlying(event.player)
        }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        preserveFlying(event.player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        preserveFlying(event.player)
    }

    private fun preserveFlying(player: Player) {
        val isFlying = player.isFlying
        Bukkit.getScheduler().runTask(plugin) { ->
            val canFly = player.isAllowedToFly() || player.gameMode in setOf(GameMode.CREATIVE, GameMode.SPECTATOR)
            player.allowFlight = canFly
            player.isFlying = canFly && isFlying
        }
    }
}
