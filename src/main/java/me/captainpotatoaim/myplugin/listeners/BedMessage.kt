package me.captainpotatoaim.myplugin.listeners

import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedLeaveEvent

class BedMessage : Listener {

    @EventHandler
    fun onPlayerWake(event: PlayerBedLeaveEvent) {
        val player = event.getPlayer()
        val server = player.server
        if (player.playerTime == 24000L) {
            server.broadcast(Component.text(player.displayName().toString() + " slept through the night. Thanks!"))
        } else {
            val random = (Math.random() * 2).toInt()
            when (random) {
                0 -> player.sendMessage("Why did you wake up?")
                1 -> player.sendMessage("Are you having trouble sleeping?")
            }
        }
    }
}
