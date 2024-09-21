package me.captainpotatoaim.myplugin.util

import com.jeff_media.customblockdata.CustomBlockData
import me.captainpotatoaim.myplugin.Initializer
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

object PDCManager {

    fun <P, C> setData(
        itemStack: ItemStack,
        key: String,
        dataType: PersistentDataType<P?, C?>,
        data: C & Any
    ) {
        val meta = checkNotNull(itemStack.itemMeta)
        setData(meta.persistentDataContainer, key, dataType, data)
        itemStack.setItemMeta(meta)
    }

    fun <P, C> setData(
        entity: Entity,
        key: String,
        dataType: PersistentDataType<P?, C?>,
        data: C & Any
    ) {
        setData(getContainer(entity), key, dataType, data)
    }

    private fun <P, C> setData(
        container: PersistentDataContainer,
        key: String,
        dataType: PersistentDataType<P?, C?>,
        data: C & Any
    ) {
        container.set(createKey(key), dataType, data)
    }

    fun <P, C> getData(itemStack: ItemStack, key: String, dataType: PersistentDataType<P?, C?>): C? =
        getData(getContainer(itemStack), key, dataType)

    fun <P, C> getData(entity: Entity, key: String, dataType: PersistentDataType<P?, C?>): C? =
        getData(getContainer(entity), key, dataType)

    fun <P, C> getData(block: Block, key: String, dataType: PersistentDataType<P?, C?>): C? =
        getData(getContainer(block), key, dataType)

    private fun <P, C> getData(
        container: PersistentDataContainer,
        key: String,
        dataType: PersistentDataType<P?, C?>
    ): C? = container.get(createKey(key), dataType)

    fun copyCustomData(from: ItemStack, to: Entity) {
        getContainer(from).copyTo(getContainer(to), true)
    }

    fun copyCustomData(from: ItemStack, to: Block) {
        copyTo(getContainer(from), getContainer(to))
    }

    fun copyCustomData(from: Block, to: ItemStack) {
        copyTo(getContainer(from), getContainer(to))
    }

    private fun createKey(key: String): NamespacedKey = NamespacedKey(Initializer.getPlugin(), key)

    private fun getContainer(itemStack: ItemStack): PersistentDataContainer {
        val meta = itemStack.itemMeta
        requireNotNull(meta) { "ItemMeta is null" }
        return meta.persistentDataContainer
    }

    private fun getContainer(entity: Entity): PersistentDataContainer = entity.persistentDataContainer

    private fun getContainer(block: Block): PersistentDataContainer = CustomBlockData(block, Initializer.getPlugin())

    /**
     * This method is needed, because [io.papermc.paper.persistence.PersistentDataContainerView.copyTo] doesn't work with
     * containers of type [CustomBlockData]
     */
    private fun copyTo(from: PersistentDataContainer, to: PersistentDataContainer) {
        from.keys.forEach { key ->
            val dataType = CustomBlockData.getDataType(from, key) ?: return
            val value = from.get(key, dataType) ?: return
            @Suppress("UNCHECKED_CAST")
            to.set(key, dataType as PersistentDataType<Any, Any>, value)
        }
    }
}
