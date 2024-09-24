package me.captainpotatoaim.myplugin.util

import com.jeff_media.morepersistentdatatypes.DataType
import org.bukkit.Location
import org.bukkit.entity.Entity
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
    PDCManager.setData(this, "experience_drop", PersistentDataType.INTEGER, amount)
}

/**
 * Get the amount of experience an entity drops upon death
 */
fun Entity.getExperienceDrop(): Int? {
    return PDCManager.getData(this, "experience_drop", PersistentDataType.INTEGER)
}

/**
 * Set the items an entity drops upon death
 */
fun Entity.setItemDrops(items: List<ItemStack>) {
    PDCManager.setData(this, "item_drops", DataType.asList(DataType.ITEM_STACK), items)
}

/**
 * Get the items an entity drops upon death
 */
fun Entity.getItemDrops(): List<ItemStack>? {
    return PDCManager.getData(this, "item_drops", DataType.asList(DataType.ITEM_STACK))
}
