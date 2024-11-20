package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.PluginState
import com.github.anviks.vixplugin.util.isOfCustomType
import com.github.anviks.vixplugin.util.setCustomType
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.AQUA
import net.kyori.adventure.text.format.TextDecoration.BOLD
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class Mjolnir(
    private val plugin: Plugin,
    private val pluginState: PluginState,
) : CustomItem, Listener {

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
}