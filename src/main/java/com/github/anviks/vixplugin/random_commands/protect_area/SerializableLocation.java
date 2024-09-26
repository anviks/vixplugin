package com.github.anviks.vixplugin.random_commands.protect_area;

import kotlinx.serialization.Serializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Serializable
public class SerializableLocation {
    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public SerializableLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public SerializableLocation(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Location toLocation() {
        return new Location(
                Bukkit.getServer().getWorld(world),
                x,
                y,
                z
        );
    }
}
