package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.util.sendBulkToggleMessage
import com.github.anviks.vixplugin.util.split
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class GodModeCommand(private val plugin: JavaPlugin) : CustomCommand {

    override fun register() {
        CommandAPICommand("god-mode")
            .withAliases("god")
            .withPermission("vixplugin.commands.admin")
            .split(
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
        val enable = targets.any { !it.isInvulnerable }

        val targetMsg: String
        val senderMsg: String
        val color: NamedTextColor

        if (enable) {
            targetMsg = "You are now in god mode."
            senderMsg = "Enabled god-mode for "
            color = GREEN
        } else {
            targetMsg = "You are no longer in god mode."
            senderMsg = "Disabled god-mode for "
            color = RED
        }

        for (target in targets) {
            target.isInvulnerable = enable
            target.sendMessage(text(targetMsg, color))
        }

        sendBulkToggleMessage(targets, sender, senderMsg, color)
    }
}
