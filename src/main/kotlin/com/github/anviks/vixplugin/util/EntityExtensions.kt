package com.github.anviks.vixplugin.util

import com.github.anviks.vixplugin.util.PDCManager.getPDCData
import com.github.anviks.vixplugin.util.PDCManager.setPDCData
import com.jeff_media.morepersistentdatatypes.DataType
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.asin
import kotlin.math.atan2

/**
 * Does what Entity#lookAt(Location) should do.
 */
fun Entity.face(targetLocation: Location) {
    val loc = this.location
    val direction = targetLocation.toVector().subtract(loc.toVector()).normalize()

    // Calculate the yaw and pitch to face the player
    val yaw = Math.toDegrees(atan2(direction.z, direction.x)) - 90
    val pitch = Math.toDegrees(-asin(direction.y))

    loc.yaw = yaw.toFloat()
    loc.pitch = pitch.toFloat()
    this.teleport(loc) // Teleport the entity to the updated location to apply the yaw/pitch
}

/**
 * Set the amount of experience an entity drops upon death
 */
fun Entity.setExperienceDrop(amount: Int) {
    this.setPDCData("experience_drop", PersistentDataType.INTEGER, amount)
}

/**
 * Get the amount of experience an entity drops upon death
 */
fun Entity.getExperienceDrop(): Int? {
    return this.getPDCData("experience_drop", PersistentDataType.INTEGER)
}

/**
 * Set the items an entity drops upon death
 */
fun Entity.setItemDrops(items: List<ItemStack>) {
    this.setPDCData("item_drops", DataType.asList(DataType.ITEM_STACK), items)
}

/**
 * Get the items an entity drops upon death
 */
fun Entity.getItemDrops(): List<ItemStack>? {
    return this.getPDCData("item_drops", DataType.asList(DataType.ITEM_STACK))
}

/**
 * Check if a player is allowed to fly
 */
fun Player.isAllowedToFly(): Boolean {
    return this.getPDCData("allow_flight", PersistentDataType.BOOLEAN) == true
}

/**
 * Set player allowed to fly status
 */
fun Player.setAllowedToFly(allowed: Boolean) {
    this.setPDCData("allow_flight", PersistentDataType.BOOLEAN, allowed)
}

/**
 * Check if a player is currently vanished
 */
fun Player.isVanished(): Boolean {
    return this.getPDCData("vanished", PersistentDataType.BOOLEAN) == true
}

/**
 * Set player vanished status
 */
fun Player.setVanished(vanished: Boolean) {
    this.setPDCData("vanished", PersistentDataType.BOOLEAN, vanished)
}
