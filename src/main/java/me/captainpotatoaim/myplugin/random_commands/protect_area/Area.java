package me.captainpotatoaim.myplugin.random_commands.protect_area;


import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record Area(Location start, Location end) {
    boolean contains(Location location) {
        int startX = start.getBlockX();
        int startY = start.getBlockY();
        int startZ = start.getBlockZ();

        int endX = end.getBlockX();
        int endY = end.getBlockY();
        int endZ = end.getBlockZ();

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return ((startX <= x && x <= endX) || (startX >= x && x >= endX))
                && ((startY <= y && y <= endY) || (startY >= y && y >= endY))
                && ((startZ <= z && z <= endZ) || (startZ >= z && z >= endZ));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Area area = (Area) o;

        if (!Objects.equals(start, area.start)) return false;
        return Objects.equals(end, area.end);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Area{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
