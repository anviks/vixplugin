package com.github.anviks.vixplugin.listeners

import com.github.anviks.vixplugin.util.isVanished
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinMessage : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        var message = event.joinMessage()

        if (event.player.isVanished()) {
            message = null
        } else if (message != null) {
            message = message.append(text(", tell them to leave"))
        }

        event.joinMessage(message)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        var message = event.quitMessage()

        if (event.player.isVanished()) {
            message = null
        } else if (message != null) {
            message = text("Good! ", YELLOW).append(message)
        }

        event.quitMessage(message)
    }
}
