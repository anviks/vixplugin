package me.captainpotatoaim.myplugin.listeners

import me.captainpotatoaim.myplugin.util.getExperienceDrop
import me.captainpotatoaim.myplugin.util.getItemDrops
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityListener : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        event.droppedExp = event.entity.getExperienceDrop() ?: event.droppedExp

        val overriddenDrops = event.entity.getItemDrops()
        if (overriddenDrops != null) {
            event.drops.clear()
            event.drops.addAll(overriddenDrops)
        }
    }
}
