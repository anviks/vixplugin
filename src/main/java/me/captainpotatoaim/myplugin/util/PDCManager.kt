package me.captainpotatoaim.myplugin.util;

import com.jeff_media.customblockdata.CustomBlockData;
import io.papermc.paper.persistence.PersistentDataContainerView;
import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class PDCManager {

    public static <P, C> void setData(ItemStack itemStack, String key, PersistentDataType<P, C> dataType, C data) {
        var meta = itemStack.getItemMeta();
        assert meta != null;
        setData(meta.getPersistentDataContainer(), key, dataType, data);
        itemStack.setItemMeta(meta);
    }

    public static <P, C> void setData(Entity entity, String key, PersistentDataType<P, C> dataType, C data) {
        setData(getContainer(entity), key, dataType, data);
    }

    private static <P, C> void setData(PersistentDataContainer container, String key, PersistentDataType<P, C> dataType, C data) {
        container.set(createKey(key), dataType, data);
    }

    public static <P, C> Optional<C> getData(ItemStack itemStack, String key, PersistentDataType<P, C> dataType) {
        return getData(getContainer(itemStack), key, dataType);
    }

    public static <P, C> Optional<C> getData(Entity entity, String key, PersistentDataType<P, C> dataType) {
        return getData(getContainer(entity), key, dataType);
    }

    public static <P, C> Optional<C> getData(Block block, String key, PersistentDataType<P, C> dataType) {
        return getData(getContainer(block), key, dataType);
    }

    private static <P, C> Optional<C> getData(PersistentDataContainer container, String key, PersistentDataType<P, C> dataType) {
        return Optional.ofNullable(container.get(createKey(key), dataType));
    }

    public static void copyCustomData(ItemStack from, Entity to) {
        getContainer(from).copyTo(getContainer(to), true);
    }

    public static void copyCustomData(ItemStack from, Block to) {
        copyTo(getContainer(from), getContainer(to));
    }

    public static void copyCustomData(Block from, ItemStack to) {
        copyTo(getContainer(from), getContainer(to));
    }

    private static NamespacedKey createKey(String key) {
        return new NamespacedKey(Initializer.getPlugin(), key);
    }

    private static PersistentDataContainer getContainer(ItemStack itemStack) {
        var meta = itemStack.getItemMeta();
        if (meta == null) throw new IllegalArgumentException("ItemMeta is null");
        return meta.getPersistentDataContainer();
    }

    private static PersistentDataContainer getContainer(Entity entity) {
        return entity.getPersistentDataContainer();
    }

    private static PersistentDataContainer getContainer(Block block) {
        return new CustomBlockData(block, Initializer.getPlugin());
    }

    /**
     * This method is needed, because {@link PersistentDataContainerView#copyTo(PersistentDataContainer, boolean)} doesn't work with
     * containers of type {@link CustomBlockData}
     */
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
}
