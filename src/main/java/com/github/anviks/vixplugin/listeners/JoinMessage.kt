package com.github.anviks.vixplugin.listeners

import com.github.anviks.vixplugin.util.isVanished
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import java.util.Objects

class JoinMessage(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.isVanished()) {
            event.joinMessage(null)
        } else {
            event.joinMessage(
                Objects.requireNonNull<Component?>(event.joinMessage()).append(text(", tell them to leave"))
            )
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        if (event.player.isVanished()) {
            event.quitMessage(null)
        } else {
            event.quitMessage(
                text("Good! ", NamedTextColor.YELLOW)
                    .append(Objects.requireNonNull<Component?>(event.quitMessage()))
            )
        }
    }
}
