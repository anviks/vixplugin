package me.captainpotatoaim.myplugin.custom_items;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.util.StringHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CustomItem {
    public static final NamespacedKey CUSTOM_ITEM_KEY = new NamespacedKey(Initializer.getPlugin(), "custom_item_type");

    public static boolean isOfType(ItemStack item, Class<?> clazz) {
        String identifier = classToIdentifier(clazz);
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        var itemIdentifier = container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING);

        return itemIdentifier != null && itemIdentifier.equals(identifier);
    }

    public static boolean isOfType(Entity entity, Class<?> clazz) {
        String identifier = classToIdentifier(clazz);
        var container = entity.getPersistentDataContainer();
        var itemIdentifier = container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING);

        return itemIdentifier != null && itemIdentifier.equals(identifier);
    }

    public static void setType(ItemStack item, Class<?> clazz) {
        String identifier = classToIdentifier(clazz);
        var meta = item.getItemMeta();
        assert meta != null;
        var container = meta.getPersistentDataContainer();
        container.set(CUSTOM_ITEM_KEY, PersistentDataType.STRING, identifier);
        item.setItemMeta(meta);
    }

    public static void setType(Entity entity, Class<?> clazz) {
        String identifier = classToIdentifier(clazz);
        var container = entity.getPersistentDataContainer();
        container.set(CUSTOM_ITEM_KEY, PersistentDataType.STRING, identifier);
    }

    public static void copyCustomData(ItemStack from, Entity to) {
        var itemMeta = from.getItemMeta();
        if (itemMeta == null) throw new IllegalArgumentException("ItemMeta is null");
        var itemPDC = itemMeta.getPersistentDataContainer();
        var entityPDC = to.getPersistentDataContainer();
        itemPDC.copyTo(entityPDC, true);
    }

    private static String classToIdentifier(Class<?> clazz) {
        return StringHelper.camelCaseToSnakeCase(clazz.getSimpleName());
    }
}
