package me.captainpotatoaim.myplugin.custom_items.grenade

import me.captainpotatoaim.myplugin.Initializer
import me.captainpotatoaim.myplugin.custom_items.CustomItem
import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ExpBottleEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.*

class ThrownGrenade : Listener {

    private val liveGrenades = HashMap<UUID, BukkitTask>()

    @EventHandler
    fun onItemDropped(event: PlayerDropItemEvent) {
        val itemDrop = event.itemDrop

        if (CustomItem.isOfType(itemDrop.itemStack, Grenade::class.java)) {
            val task =
                Runnable { event.player.world.createExplosion(itemDrop.location, 10f) }
            val uuid = itemDrop.uniqueId
            val bukkitTask = Bukkit.getScheduler()
                .runTaskLater(Initializer.getPlugin(), task, 100)

            liveGrenades[uuid] = bukkitTask
        }
    }

    @EventHandler
    fun onItemPicked(event: EntityPickupItemEvent) {
        val item = event.item

        if (CustomItem.isOfType(item.itemStack, Grenade::class.java)) {
            this.tryCancelGrenadeExplosion(item)
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val item = event.entity as? Item ?: return
        val itemStack: ItemStack = item.itemStack
        if (CustomItem.isOfType(itemStack, Grenade::class.java)) {
            // Item#isDead returns true only after the final damage event is processed
            Bukkit.getScheduler().runTaskLater(Initializer.getPlugin(), Runnable {
                if (item.isDead) this.tryCancelGrenadeExplosion(item)
            }, 1)
        }
    }

    @EventHandler
    fun onXPBottleThrown(event: ExpBottleEvent) {
        if (CustomItem.isOfType(event.entity.item, Grenade::class.java)) {
            event.experience = 0
            event.showEffect = false
            event.entity.world.createExplosion(event.entity.location, 5f)
        }
    }

    private fun tryCancelGrenadeExplosion(item: Item) {
        val itemId = item.uniqueId
        val task = liveGrenades.remove(itemId)
        task?.cancel()
    }
}