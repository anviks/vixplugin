package me.captainpotatoaim.myplugin.random_commands.protect_area;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record Area(SerializableLocation start, SerializableLocation end) {
    boolean contains(SerializableLocation location) {
        int startX = start.getX();
        int startY = start.getY();
        int startZ = start.getZ();

        int endX = end.getX();
        int endY = end.getY();
        int endZ = end.getZ();

        int x = location.getX();
        int y = location.getY();
        int z = location.getZ();

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
