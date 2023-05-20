package me.captainpotatoaim.myplugin.util;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.SecureRandom;

public class Tagger {
    public static final NamespacedKey key = new NamespacedKey(JavaPlugin.getPlugin(Initializer.class),
            "ecb0fceb1a8f8f9b449c5ae7cdaee0216988dbf6f3f5d76743f98e73d52cdc21");

    public static void tagItem(ItemStack item, byte[] secret) {
        var meta = item.getItemMeta();
        assert meta != null;
        var container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.BYTE_ARRAY, secret);
        item.setItemMeta(meta);
    }

    public static void tagEntity(Entity entity, byte[] secret) {
        var container = entity.getPersistentDataContainer();
        container.set(key, PersistentDataType.BYTE_ARRAY, secret);
    }

    public static byte[] generateSecret() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        
        return randomBytes;
    }
}
