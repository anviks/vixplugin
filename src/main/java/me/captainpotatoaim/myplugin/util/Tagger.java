package me.captainpotatoaim.myplugin.util;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class Tagger {
    public static final NamespacedKey CUSTOM_ITEM_KEY = new NamespacedKey(Initializer.plugin, "custom_item_type");

    public static boolean hasIdentifier(ItemStack item, String identifier) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        var itemIdentifier = container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING);
        return itemIdentifier != null && itemIdentifier.equals(identifier);
    }

    public static boolean hasIdentifier(Entity entity, String identifier) {
        var container = entity.getPersistentDataContainer();
        var itemIdentifier = container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING);
        return itemIdentifier != null && itemIdentifier.equals(identifier);
    }

    public static void addIdentifier(ItemStack item, String identifier) {
        var meta = item.getItemMeta();
        assert meta != null;
        var container = meta.getPersistentDataContainer();
        container.set(CUSTOM_ITEM_KEY, PersistentDataType.STRING, identifier);
        item.setItemMeta(meta);
    }

    public static void addIdentifier(Entity entity, String identifier) {
        var container = entity.getPersistentDataContainer();
        container.set(CUSTOM_ITEM_KEY, PersistentDataType.STRING, identifier);
    }
}
