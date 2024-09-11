package me.captainpotatoaim.myplugin.random_commands.protect_area;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProtectedAreas {
    static final Map<String, Area> protectedAreas = new HashMap<>();
    static final String FILE_PATH = Initializer.plugin.getDataFolder().getPath() + "/protected_areas.json";

    public static void saveAreas() {
        try (FileWriter writer1 = new FileWriter(FILE_PATH)) {
            Bukkit.broadcastMessage("reached saving");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(convertToMap(), writer1);

            Bukkit.broadcastMessage("Data written to the random file successfully!");
        } catch (IOException e) {
            Bukkit.broadcastMessage("Error writing to the JSON file: " + e.getMessage());
        }
    }

    public static void loadAreas() {
        Gson gson = new GsonBuilder().create();

        try (FileReader reader = new FileReader(FILE_PATH)) {
            // Read JSON from file and parse it into your custom class
            Map<String, Map<String, Object>> data = gson.fromJson(reader, HashMap.class);

            for (var entry : data.entrySet()) {
                Map<String, Object> value = entry.getValue();
                World world = Bukkit.getServer().getWorld((String) value.get("world"));

                Map<String, Double> startCoords = (Map<String, Double>) value.get("start");
                Map<String, Double> endCoords = (Map<String, Double>) value.get("end");

                Location start = new Location(
                        world,
                        startCoords.get("X"),
                        startCoords.get("Y"),
                        startCoords.get("Z")
                );

                Location end = new Location(
                        world,
                        endCoords.get("X"),
                        endCoords.get("Y"),
                        endCoords.get("Z")
                );

                Area area = new Area(start, end);
                protectedAreas.put(entry.getKey(), area);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Map<String, Object>> convertToMap() {
        Map<String, Map<String, Object>> areas = new HashMap<>();

        for (var entry : protectedAreas.entrySet()) {
            Map<String, Object> something = new HashMap<>();
            areas.put(entry.getKey(), something);

            Location startLocation = entry.getValue().start();
            Location endLocation = entry.getValue().end();

            something.put("world", startLocation.getWorld().getName());

            something.put("start", Map.of(
                    "X", startLocation.getBlockX(),
                    "Y", startLocation.getBlockY(),
                    "Z", startLocation.getBlockZ()
            ));

            something.put("end", Map.of(
                    "X", endLocation.getBlockX(),
                    "Y", endLocation.getBlockY(),
                    "Z", endLocation.getBlockZ()
            ));
        }

        return areas;
    }

    static boolean isProtected(Location location) {
        for (Area area : protectedAreas.values()) {
            if (area.contains(location)) {
                return true;
            }
        }

        return false;
    }
}
