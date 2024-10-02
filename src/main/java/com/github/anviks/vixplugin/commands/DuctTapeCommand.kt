package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.PluginState
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.arguments.TimeArgument
import dev.jorel.commandapi.executors.CommandArguments
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import kotlin.random.Random


class DuctTapeCommand(
    private val plugin: JavaPlugin,
    private val pluginState: PluginState,
) : CustomCommand, Listener {

    init {
        pluginState.tapedPlayers.forEach {
            val removeTapeAfter = (it.value - Clock.System.now()).inWholeSeconds * 20
            scheduleTapeRemoval(it.key, removeTapeAfter)
        }
    }

    override fun register() {
        CommandAPICommand("duct-tape")
            .withAliases("tape")
            .withPermission("vixplugin.commands.moderator")
            .withArguments(EntitySelectorArgument.ManyPlayers("targets"))
            .withArguments(TimeArgument("duration"))
            .executes(this::run)
            .register(this.plugin)
    }

    fun run(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!
        val duration = arguments.get("duration") as Long
        val removeTapeAt = Clock.System.now().plus(duration / 20, DateTimeUnit.SECOND)

        for (target in targets) {
            sender.sendMessage(
                target
                    .displayName()
                    .append(text(" has been duct-taped until ", GREEN))
                    .append(text(removeTapeAt.toString(), GREEN, BOLD, UNDERLINED))
            )

            pluginState.tapedPlayers[target.uniqueId] = removeTapeAt
            scheduleTapeRemoval(target.uniqueId, duration)
        }
    }

    private fun scheduleTapeRemoval(playerUUID: UUID, afterTicks: Long) {
        Bukkit.getScheduler().runTaskLater(plugin, { ->
            pluginState.tapedPlayers.remove(playerUUID)
        }, afterTicks)
    }

    @EventHandler
    fun onChatUse(event: AsyncChatEvent) {
        if (pluginState.tapedPlayers.contains(event.getPlayer().uniqueId)) {
            val message = (event.message() as TextComponent).content()
            val newMessage = StringBuilder()

            for (letter in message.toCharArray()) {
                if (letter == ' ') {
                    newMessage.append(' ')
                } else {
                    newMessage.append(if (Random.nextBoolean()) 'm' else 'f')
                }
            }

            event.message(text(newMessage.toString()))
        }
    }
}
