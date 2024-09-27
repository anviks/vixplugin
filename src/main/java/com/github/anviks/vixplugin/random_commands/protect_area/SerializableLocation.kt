package com.github.anviks.vixplugin.random_commands.protect_area

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location

@Serializable
class SerializableLocation {
    val world: String
    val x: Int
    val y: Int
    val z: Int

    constructor(location: Location) {
        this.world = location.world.name
        this.x = location.blockX
        this.y = location.blockY
        this.z = location.blockZ
    }

    constructor(world: String, x: Int, y: Int, z: Int) {
        this.world = world
        this.x = x
        this.y = y
        this.z = z
    }

    fun toLocation(): Location {
        return Location(
            Bukkit.getServer().getWorld(world),
            x.toDouble(),
            y.toDouble(),
            z.toDouble()
        )
    }
}
