package com.github.anviks.vixplugin.random_commands.protect_area

import kotlinx.serialization.Serializable

@Serializable
data class Area(private val start: SerializableLocation, private val end: SerializableLocation) {
    fun contains(location: SerializableLocation): Boolean {
        val startX = start.x
        val startY = start.y
        val startZ = start.z

        val endX = end.x
        val endY = end.y
        val endZ = end.z

        val x = location.x
        val y = location.y
        val z = location.z

        return ((startX <= x && x <= endX) || (startX >= x && x >= endX))
                && ((startY <= y && y <= endY) || (startY >= y && y >= endY))
                && ((startZ <= z && z <= endZ) || (startZ >= z && z >= endZ))
    }
}
