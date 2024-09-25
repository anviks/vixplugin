package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.Initializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ExpBottleEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

class Grenade : CustomItem(), Listener {

    private val liveGrenades = HashMap<UUID, BukkitTask>()

    override fun getItem(count: Int): ItemStack {
        val grenade = ItemStack(Material.EXPERIENCE_BOTTLE, count)
        val grenadeMeta = checkNotNull(grenade.itemMeta)
        grenadeMeta.displayName(Component.text("GRENADE", NamedTextColor.RED))
        grenadeMeta.lore(listOf(Component.text("Toss it at someone.")))
        grenade.setItemMeta(grenadeMeta)
        setType(grenade, Grenade::class.java)

        return grenade
    }

    @EventHandler
    fun onItemDropped(event: PlayerDropItemEvent) {
        val itemDrop = event.itemDrop

        if (isOfType(itemDrop.itemStack, Grenade::class.java)) {
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

        if (isOfType(item.itemStack, Grenade::class.java)) {
            this.tryCancelGrenadeExplosion(item)
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val item = event.entity as? Item ?: return
        val itemStack: ItemStack = item.itemStack
        if (isOfType(itemStack, Grenade::class.java)) {
            // Item#isDead returns true only after the final damage event is processed
            Bukkit.getScheduler().runTaskLater(Initializer.getPlugin(), Runnable {
                if (item.isDead) this.tryCancelGrenadeExplosion(item)
            }, 1)
        }
    }

    @EventHandler
    fun onXPBottleThrown(event: ExpBottleEvent) {
        if (isOfType(event.entity.item, Grenade::class.java)) {
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