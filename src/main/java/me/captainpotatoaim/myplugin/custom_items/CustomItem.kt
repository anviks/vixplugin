package me.captainpotatoaim.myplugin.custom_items;

import me.captainpotatoaim.myplugin.util.PDCManager;
import me.captainpotatoaim.myplugin.util.StringHelper;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;


public abstract class CustomItem {

    private static final String CUSTOM_ITEM_KEY = "custom_item_type";

    public abstract ItemStack getItem(int count);

    public static boolean isOfType(ItemStack item, Class<?> clazz) {
        return holderIsOfType(item, clazz);
    }

    public static boolean isOfType(Entity entity, Class<?> clazz) {
        return holderIsOfType(entity, clazz);
    }

    public static boolean isOfType(Block block, Class<?> clazz) {
        return holderIsOfType(block, clazz);
    }

    private static boolean holderIsOfType(Object object, Class<?> clazz) {
        String identifier = classToIdentifier(clazz);
        Optional<String> data = switch (object) {
            case ItemStack itemStack -> PDCManager.getData(itemStack, CUSTOM_ITEM_KEY, PersistentDataType.STRING);
            case Entity entity -> PDCManager.getData(entity, CUSTOM_ITEM_KEY, PersistentDataType.STRING);
            case Block block -> PDCManager.getData(block, CUSTOM_ITEM_KEY, PersistentDataType.STRING);
            case null, default -> {
                assert object != null;
                throw new IllegalArgumentException("Unsupported type: " + object.getClass());
            }
        };

        return data.isPresent() && data.get().equals(identifier);
    }

    public static void setType(ItemStack item, Class<?> clazz) {
        PDCManager.setData(item, CUSTOM_ITEM_KEY, PersistentDataType.STRING, classToIdentifier(clazz));
    }

    public static void setType(Entity entity, Class<?> clazz) {
        PDCManager.setData(entity, CUSTOM_ITEM_KEY, PersistentDataType.STRING, classToIdentifier(clazz));
    }

    private static String classToIdentifier(Class<?> clazz) {
        return StringHelper.INSTANCE.camelCaseToSnakeCase(clazz.getSimpleName());
    }
}
