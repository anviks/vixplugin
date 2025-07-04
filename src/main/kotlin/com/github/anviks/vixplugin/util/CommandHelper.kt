package com.github.anviks.vixplugin.util

import dev.jorel.commandapi.CommandAPICommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


/**
 * Sends a message to the sender indicating that a bulk toggle action has been performed on multiple targets.
 */
fun sendBulkToggleMessage(targets: Collection<Player>, sender: CommandSender, senderMsg: String, color: NamedTextColor) {
    val otherTargets = targets.toMutableList()
    if (sender is Player) otherTargets.remove(sender)

    if (otherTargets.isNotEmpty()) {
        sender.sendMessage(
            text()
                .append(text(senderMsg))
                .append(text(otherTargets.joinToString { it.name }))
                .append(text("."))
                .color(color)
                .build()
        )
    }
}

/**
 * Applies multiple "overloads" to a CommandAPICommand instance.
 * This allows to define different behaviours, executors, or arguments for the same command name.
 * This differs from CommandAPI's subcommands in that subcommands require an additional literal string argument
 * to be specified (the subcommand's name), while overloads do not.
 */
fun CommandAPICommand.withOverloads(vararg overloads: (CommandAPICommand) -> CommandAPICommand): List<CommandAPICommand> {
    val commands = mutableListOf<CommandAPICommand>()
    for (func in overloads) {
        commands.add(func(this.copy()))
    }
    return commands
}
