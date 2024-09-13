package me.captainpotatoaim.myplugin.custom_items;

import me.captainpotatoaim.myplugin.util.StringHelper;
import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class CustomItem {
    public static boolean isOfType(ItemStack item, Class<?> clazz) {
        return Tagger.hasIdentifier(item, classToIdentifier(clazz));
    }

    public static boolean isOfType(Entity entity, Class<?> clazz) {
        return Tagger.hasIdentifier(entity, classToIdentifier(clazz));
    }

    public static void setType(ItemStack item, Class<?> clazz) {
        Tagger.addIdentifier(item, classToIdentifier(clazz));
    }

    public static void setType(Entity entity, Class<?> clazz) {
        Tagger.addIdentifier(entity, classToIdentifier(clazz));
    }

//    public static void copyCustomData(ItemStack from, Entity to) {
//        var itemMeta = from.getItemMeta();
//        if (itemMeta == null) throw new IllegalArgumentException("ItemMeta is null");
//        var itemPDC = itemMeta.getPersistentDataContainer();
//        var entityPDC = to.getPersistentDataContainer();
//        itemPDC.copyTo(entityPDC, true);
//    }

    private static String classToIdentifier(Class<?> clazz) {
        return StringHelper.camelCaseToSnakeCase(clazz.getSimpleName());
    }
}
