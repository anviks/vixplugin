package com.github.anviks.vixplugin.listeners

import net.kyori.adventure.text.Component.text
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedLeaveEvent
import kotlin.random.Random

class BedMessage : Listener {

    @EventHandler
    fun onPlayerWake(event: PlayerBedLeaveEvent) {
        val player = event.getPlayer()
        val server = player.server
        if (player.playerTime == 24000L) {
            server.broadcast(player.displayName().append(text(" slept through the night. Thanks!")))
        } else {
            player.sendMessage(
                if (Random.nextBoolean()) "Why did you wake up?"
                else "Are you having trouble sleeping?"
            )
        }
    }
}
