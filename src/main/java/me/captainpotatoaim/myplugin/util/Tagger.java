package me.captainpotatoaim.myplugin.util;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.apache.commons.codec.binary.Hex;

import java.security.SecureRandom;

public class Tagger {
    public static final NamespacedKey KEY = new NamespacedKey(Initializer.plugin,
            "custom-item");

    public static boolean hasIdentifier(ItemStack item, String identifier) {
        var meta = item.getItemMeta();
        assert meta != null;
        var container = meta.getPersistentDataContainer();
        var itemIdentifier = container.get(KEY, PersistentDataType.STRING);
        return itemIdentifier != null && itemIdentifier.equals(identifier);
    }

    public static boolean hasIdentifier(Entity entity, String identifier) {
        var container = entity.getPersistentDataContainer();
        var itemIdentifier = container.get(KEY, PersistentDataType.STRING);
        return itemIdentifier != null && itemIdentifier.equals(identifier);
    }

    public static void tagItem(ItemStack item, String identifier) {
        var meta = item.getItemMeta();
        assert meta != null;
        var container = meta.getPersistentDataContainer();
        container.set(KEY, PersistentDataType.STRING, identifier);
        item.setItemMeta(meta);
    }

    public static void tagEntity(Entity entity, String identifier) {
        var container = entity.getPersistentDataContainer();
        container.set(KEY, PersistentDataType.STRING, identifier);
    }

    private static String generateIdentifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return new String(Hex.encodeHex(randomBytes));
    }

    public static String getIdentifier(String item) {
        FileConfiguration config = Initializer.plugin.getConfig();
        String identifier = config.getString("identifiers." + item);

        if (identifier == null || identifier.length() != 64) {
            identifier = generateIdentifier();
            config.set("identifiers." + item, identifier);
            Initializer.plugin.saveConfig();
        }

        return identifier;
    }
}
