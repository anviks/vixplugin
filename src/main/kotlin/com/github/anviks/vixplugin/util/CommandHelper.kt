package com.github.anviks.vixplugin.util

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


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
