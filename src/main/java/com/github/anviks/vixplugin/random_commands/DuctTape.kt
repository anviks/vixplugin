package com.github.anviks.vixplugin.random_commands

import com.github.anviks.vixplugin.CustomCommand
import com.github.anviks.vixplugin.PluginState
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.arguments.TimeArgument
import dev.jorel.commandapi.executors.CommandArguments
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.time.LocalDateTime
import java.util.Random


class DuctTape(
    private val plugin: JavaPlugin,
    private val pluginState: PluginState,
) : CustomCommand, Listener {

    private val randomizer = Random()

    override fun register() {
        CommandAPICommand("duct-tape")
            .withPermission(CommandPermission.OP)
            .withArguments(EntitySelectorArgument.ManyPlayers("targets"))
            .withArguments(TimeArgument("duration"))
            .executes(this::run)
            .register(this.plugin)
    }

    fun run(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!
        val duration = arguments.getByClass("duration", Int::class.java)!!.toLong()
        val removeTapeAt = LocalDateTime.now().plusSeconds(duration / 20)
        val scheduler = Bukkit.getScheduler()

        for (target in targets) {
            pluginState.tapedPlayers.put(target.uniqueId, removeTapeAt)
            scheduler.runTaskLater(this.plugin, Runnable {
                pluginState.tapedPlayers.remove(target.uniqueId)
            }, duration)
        }
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
                    newMessage.append(if (randomizer.nextBoolean()) 'm' else 'f')
                }
            }

            event.message(Component.text(newMessage.toString()))
        }
    }
}
