package com.github.anviks.vixplugin.commands.protect_area

import com.github.anviks.vixplugin.PluginState
import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent

class BlockListener(private val pluginState: PluginState) : Listener {

    @EventHandler
    fun blockDamaged(event: BlockDamageEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockBroken(event: BlockBreakEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockExplosion(event: BlockExplodeEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockPlaced(event: BlockPlaceEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockMultiPlaced(event: BlockMultiPlaceEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockExplosionByEntity(event: EntityExplodeEvent) {
        event.blockList().removeIf { isProtected(it.location) }
    }

    private fun <T> cancelIfProtected(event: T) where T : BlockEvent, T : Cancellable {
        val location = event.block.location

        if (isProtected(location)) {
            event.isCancelled = true
        }
    }

    private fun isProtected(location: Location): Boolean {
        for (area in pluginState.protectedAreas.values) {
            if (location in area) {
                return true
            }
        }

        return false
    }
}
