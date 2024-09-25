package com.github.anviks.vixplugin.random_commands.protect_area;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.github.anviks.vixplugin.Initializer;
import org.bukkit.Location;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ProtectedAreas {
    static Map<String, Area> protectedAreas = new HashMap<>();
    static final String FILE_PATH = Initializer.getPlugin().getDataFolder().getPath() + "/protected_areas.json";
    static final Logger logger = Initializer.getPlugin().getLogger();

    public static void saveAreas() {
        try (FileWriter writer1 = new FileWriter(FILE_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(protectedAreas, writer1);
        } catch (IOException e) {
            logger.warning("Error writing to the JSON file: " + e.getMessage());
        }
    }

    public static void loadAreas() {
        Gson gson = new GsonBuilder().create();

        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type jsonStructure = new TypeToken<Map<String, Area>>() {
            }.getType();
            protectedAreas = gson.fromJson(reader, jsonStructure);
        } catch (IOException e) {
            logger.warning("Error reading from the JSON file: " + e.getMessage());
        }
    }

    static boolean isProtected(Location location) {
        var serializableLocation = new SerializableLocation(location);
        return isProtected(serializableLocation);
    }

    static boolean isProtected(SerializableLocation location) {
        for (Area area : protectedAreas.values()) {
            if (area.contains(location)) {
                return true;
            }
        }

        return false;
    }
}
