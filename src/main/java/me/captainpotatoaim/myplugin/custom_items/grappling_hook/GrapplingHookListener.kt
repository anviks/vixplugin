package me.captainpotatoaim.myplugin.custom_items.grappling_hook

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class GrapplingHookListener : Listener {
    @EventHandler
    private fun thrown(event: PlayerFishEvent) {
        val state = event.state

        if (!(state == PlayerFishEvent.State.REEL_IN || state == PlayerFishEvent.State.IN_GROUND)) {
            return
        }

        val player = event.player

        if (!CustomItem.isOfType(player.inventory.itemInMainHand, GrapplingHook::class.java)) {
            return
        }

        val hookLocation = event.hook.location
        val playerLocation = player.location

        val launchDirection = (hookLocation.subtract(playerLocation)).toVector()
        player.velocity = launchDirection.multiply(0.5f)
    }
}