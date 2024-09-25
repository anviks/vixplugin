package com.github.anviks.vixplugin.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class Moving : Listener {
    @EventHandler
    fun canMove(event: PlayerMoveEvent) {
        if (!event.getPlayer().hasPermission("vix.move")) {
            event.isCancelled = true
        }
    }
}
