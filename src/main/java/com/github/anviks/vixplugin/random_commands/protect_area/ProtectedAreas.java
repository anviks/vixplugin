package com.github.anviks.vixplugin.random_commands.protect_area;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ProtectedAreas {

    private static Map<String, Area> protectedAreas = new HashMap<>();

    public ProtectedAreas(Plugin plugin) {
    }

    static boolean isProtected(Location location) {
        var serializableLocation = new SerializableLocation(location);
        return isProtected(serializableLocation);
    }

    static boolean isProtected(SerializableLocation location) {
        for (Area area : getProtectedAreas().values()) {
            if (area.contains(location)) {
                return true;
            }
        }

        return false;
    }

    public static Map<String, Area> getProtectedAreas() {
        return protectedAreas;
    }

    public static void setProtectedAreas(Map<String, Area> protectedAreas) {
        ProtectedAreas.protectedAreas = protectedAreas;
    }
}
