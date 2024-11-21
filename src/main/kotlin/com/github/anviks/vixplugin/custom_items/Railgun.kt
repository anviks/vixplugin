package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.util.AdventureHelper
import com.github.anviks.vixplugin.util.isOfCustomType
import com.github.anviks.vixplugin.util.setCustomType
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.NamedTextColor.YELLOW
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector


class Railgun : CustomItem, Listener {

    override fun getItem(count: Int): ItemStack {
        val railGun = ItemStack(Material.TRIDENT, 1)
        val railGunMeta = railGun.itemMeta
        railGunMeta.displayName(AdventureHelper.createAlternatingColoredText("RAILGUN", GRAY, YELLOW))
        railGunMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true)
        railGun.setItemMeta(railGunMeta)
        railGun.setCustomType<Railgun>()

        return railGun
    }

    @EventHandler
    fun onTridentThrown(event: ProjectileLaunchEvent) {
        if (event.entityType != EntityType.TRIDENT) return
        val shooter = event.entity.shooter as? LivingEntity ?: return
        val equipment: EntityEquipment = shooter.equipment ?: return

        val itemInMainHand = equipment.itemInMainHand
        val itemInOffHand = equipment.itemInOffHand

        val shotTrident =
            if (itemInMainHand.type == Material.TRIDENT)
                itemInMainHand
            else
                itemInOffHand

        if (!shotTrident.isOfCustomType<Railgun>()) {
            return
        }

        if (shooter is Player && shooter.gameMode != GameMode.CREATIVE) {
            shotTrident.subtract()
        }

        event.isCancelled = true
        val shot: Vector = shooter.eyeLocation.direction
        var explosionLocation: Location = shooter.location

        explosionLocation.add(shot).add(0.0, 2.0, 0.0)

        repeat(150) {
            explosionLocation.add(shot)
            shooter.world.createExplosion(explosionLocation, 2.6f, true, true, shooter)
        }
    }
}
