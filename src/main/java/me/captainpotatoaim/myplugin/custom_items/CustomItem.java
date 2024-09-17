package me.captainpotatoaim.myplugin.custom_items;

import com.jeff_media.customblockdata.CustomBlockData;
import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.util.StringHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


public abstract class CustomItem {
    private static final NamespacedKey CUSTOM_ITEM_KEY = new NamespacedKey(Initializer.getPlugin(), "custom_item_type");

    public abstract ItemStack getItem(int count);

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

    public static boolean isOfType(Block block, Class<?> clazz) {
        String identifier = classToIdentifier(clazz);
        var container = new CustomBlockData(block, Initializer.getPlugin());
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

    public static <P, C> void addData(ItemStack itemStack, String key, PersistentDataType<P, C> dataType, C data) {
        NamespacedKey namespacedKey = new NamespacedKey(Initializer.getPlugin(), key);
        var meta = itemStack.getItemMeta();
        assert meta != null;
        var container = meta.getPersistentDataContainer();
        container.set(namespacedKey, dataType, data);
        itemStack.setItemMeta(meta);
    }

    public static <P, C> C getData(ItemStack itemStack, String key, PersistentDataType<P, C> dataType) {
        NamespacedKey namespacedKey = new NamespacedKey(Initializer.getPlugin(), key);
        var meta = itemStack.getItemMeta();
        assert meta != null;
        var container = meta.getPersistentDataContainer();

        return container.get(namespacedKey, dataType);
    }

    public static <P, C> C getData(Block block, String key, PersistentDataType<P, C> dataType) {
        NamespacedKey namespacedKey = new NamespacedKey(Initializer.getPlugin(), key);
        var container = new CustomBlockData(block, Initializer.getPlugin());

        return container.get(namespacedKey, dataType);
    }

    public static void copyCustomData(ItemStack from, Entity to) {
        var itemMeta = from.getItemMeta();
        if (itemMeta == null) throw new IllegalArgumentException("ItemMeta is null");
        var itemPDC = itemMeta.getPersistentDataContainer();
        var entityPDC = to.getPersistentDataContainer();
        itemPDC.copyTo(entityPDC, true);
    }

    public static void copyCustomData(ItemStack from, Block to) {
        ItemMeta itemMeta = from.getItemMeta();
        if (itemMeta == null) throw new IllegalArgumentException("ItemMeta is null");
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        PersistentDataContainer blockPDC = new CustomBlockData(to, Initializer.getPlugin());
        copyTo(itemPDC, blockPDC);
    }

    public static void copyCustomData(Block from, ItemStack to) {
        ItemMeta itemMeta = to.getItemMeta();
        if (itemMeta == null) throw new IllegalArgumentException("ItemMeta is null");
        PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
        PersistentDataContainer blockPDC = new CustomBlockData(from, Initializer.getPlugin());
        copyTo(blockPDC, itemPDC);
    }

    private static void copyTo(PersistentDataContainer from, PersistentDataContainer to) {
        from.getKeys().forEach((key) -> {
            PersistentDataType<?, ?> dataType = CustomBlockData.getDataType(from, key);
            if (dataType != null) {
                copyPersistentData(from, to, key, dataType);
            }
        });
    }

    private static <T, Z> void copyPersistentData(
            PersistentDataContainer from,
            PersistentDataContainer to,
            NamespacedKey key,
            PersistentDataType<T, Z> dataType
    ) {
        Z value = from.get(key, dataType);
        assert value != null;
        to.set(key, dataType, value);
    }

    private static String classToIdentifier(Class<?> clazz) {
        return StringHelper.camelCaseToSnakeCase(clazz.getSimpleName());
    }
}
