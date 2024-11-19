@file:UseContextualSerialization(UUID::class)

package com.github.anviks.vixplugin

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.UUID


@Serializable
data class PluginState(
    val protectedAreas: MutableMap<String, Area> = mutableMapOf(),
    val tapedPlayers: MutableMap<UUID, Instant> = mutableMapOf(),
    val protectedEntities: MutableMap<UUID, MutableSet<UUID>> = mutableMapOf(),
    val frozenPlayers: MutableMap<UUID, Instant> = mutableMapOf(),
)

@Serializable
data class Area(
    private val start: SerializableLocation,
    private val end: SerializableLocation
) {

    operator fun contains(location: SerializableLocation): Boolean =
        contains(location.x, location.y, location.z)

    operator fun contains(location: Location): Boolean =
        contains(location.blockX, location.blockY, location.blockZ)

    fun contains(x: Int, y: Int, z: Int): Boolean {
        val startX = start.x
        val startY = start.y
        val startZ = start.z

        val endX = end.x
        val endY = end.y
        val endZ = end.z

        return ((startX <= x && x <= endX) || (startX >= x && x >= endX))
                && ((startY <= y && y <= endY) || (startY >= y && y >= endY))
                && ((startZ <= z && z <= endZ) || (startZ >= z && z >= endZ))
    }
}

@Serializable
data class SerializableLocation(
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int,
) {

    constructor(location: Location) : this(
        location.world.name,
        location.blockX,
        location.blockY,
        location.blockZ
    )

    fun toLocation(): Location {
        return Location(
            Bukkit.getServer().getWorld(world),
            x.toDouble(),
            y.toDouble(),
            z.toDouble()
        )
    }
}

