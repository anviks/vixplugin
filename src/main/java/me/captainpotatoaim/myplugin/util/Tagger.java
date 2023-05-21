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
            "ecb0fceb1a8f8f9b449c5ae7cdaee0216988dbf6f3f5d76743f98e73d52cdc21");

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
            identifier = Tagger.generateIdentifier();
            config.set("identifiers." + item, identifier);
            Initializer.plugin.saveConfig();
        }

        return identifier;
    }
}
