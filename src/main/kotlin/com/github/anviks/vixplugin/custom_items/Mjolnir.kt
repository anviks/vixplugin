package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.PluginState
import com.github.anviks.vixplugin.util.isOfCustomType
import com.github.anviks.vixplugin.util.setCustomType
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.AQUA
import net.kyori.adventure.text.format.TextDecoration.BOLD
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityToggleGlideEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

class Mjolnir(
    private val plugin: Plugin,
    private val pluginState: PluginState,
) : CustomItem, Listener {

    private val flyingPlayers = mutableMapOf<UUID, BukkitTask>()

    override fun getItem(count: Int): ItemStack {
        val mjolnir = ItemStack(Material.TRIDENT, 1)
        val mjolnirMeta = mjolnir.itemMeta
        mjolnirMeta.displayName(text("Mjölnir", AQUA, BOLD))
        mjolnirMeta.addEnchant(Enchantment.LOYALTY, 3, false)
        mjolnirMeta.addEnchant(Enchantment.IMPALING, 10, true)
        mjolnirMeta.addEnchant(Enchantment.CHANNELING, 10, true)
        mjolnirMeta.addEnchant(Enchantment.UNBREAKING, 3, true)
        mjolnir.setItemMeta(mjolnirMeta)
        mjolnir.setCustomType<Mjolnir>()

        return mjolnir
    }

    @EventHandler
    fun onTridentThrown(event: ProjectileLaunchEvent) {
        val trident = event.entity as? Trident ?: return
        val shooter = trident.shooter as? LivingEntity ?: return
        val equipment: EntityEquipment = shooter.equipment ?: return

        val itemInMainHand = equipment.itemInMainHand
        val itemInOffHand = equipment.itemInOffHand

        val shotTrident =
            if (itemInMainHand.type == Material.TRIDENT)
                itemInMainHand
            else
                itemInOffHand

        if (!shotTrident.isOfCustomType<Mjolnir>()) return
        stopFlight(shooter)

        object : BukkitRunnable() {
            override fun run() {
                if (trident.isDead) {
                    cancel()
                    return
                }

                val strike = trident.world.strikeLightning(trident.location)
                if (shooter is Player) strike.causingPlayer = shooter

                val protectedFrom = pluginState.protectedEntities.getOrPut(shooter.uniqueId) { mutableSetOf() }
                protectedFrom.add(strike.uniqueId)
            }
        }.runTaskTimer(plugin, 1, 1)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (item.isOfCustomType<Mjolnir>() && event.player.isGliding && event.action == Action.LEFT_CLICK_AIR) {
            val uuid = event.player.uniqueId
            val task = flyingPlayers.remove(uuid)

            if (task != null) {
                task.cancel()
            } else {
                flyingPlayers[uuid] = Bukkit.getScheduler().runTaskTimer(plugin, { ->
                    acceleratePlayer(event.player)
                }, 0, 1)
            }
        }
    }

    @EventHandler
    fun onPlayerStopGlide(event: EntityToggleGlideEvent) {
        if (!event.isGliding) {
            stopFlight(event.entity)
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        stopFlight(event.player)
    }

    private fun acceleratePlayer(player: Player) {
        val direction = player.location.direction
        val acceleration = 0.15

        val currentVelocity = player.velocity
        if (currentVelocity.length() >= 2.5) return
        val newVelocity = currentVelocity.add(direction.multiply(acceleration))
        player.velocity = newVelocity
    }

    private fun stopFlight(entity: Entity) {
        flyingPlayers.remove(entity.uniqueId)?.cancel()
    }
}