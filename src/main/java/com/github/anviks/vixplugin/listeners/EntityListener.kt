package com.github.anviks.vixplugin.listeners

import com.github.anviks.vixplugin.PluginState
import com.github.anviks.vixplugin.util.getExperienceDrop
import com.github.anviks.vixplugin.util.getItemDrops
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

class EntityListener(private val pluginState: PluginState) : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        event.droppedExp = event.entity.getExperienceDrop() ?: event.droppedExp

        val overriddenDrops = event.entity.getItemDrops()
        if (overriddenDrops != null) {
            event.drops.clear()
            event.drops.addAll(overriddenDrops)
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        val protectedFrom = pluginState.protectedEntities.getOrDefault(event.entity.uniqueId, mutableSetOf())
        if (event.damager.uniqueId in protectedFrom) {
            event.isCancelled = true
        }
    }
}
