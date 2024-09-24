package me.captainpotatoaim.myplugin.util

import com.jeff_media.customblockdata.CustomBlockData
import me.captainpotatoaim.myplugin.Initializer
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType


fun <P, C> ItemStack.setPDCData(
    key: String,
    dataType: PersistentDataType<P?, C?>,
    data: C & Any
) {
    val meta = checkNotNull(this.itemMeta)
    setPDCData(meta.persistentDataContainer, key, dataType, data)
    this.setItemMeta(meta)
}

fun <P, C> Entity.setPDCData(
    key: String,
    dataType: PersistentDataType<P?, C?>,
    data: C & Any
) {
    setPDCData(getContainer(this), key, dataType, data)
}

private fun <P, C> setPDCData(
    container: PersistentDataContainer,
    key: String,
    dataType: PersistentDataType<P?, C?>,
    data: C & Any
) {
    container.set(createKey(key), dataType, data)
}

fun <P, C> ItemStack.getPDCData(key: String, dataType: PersistentDataType<P?, C?>): C? =
    getData(getContainer(this), key, dataType)

fun <P, C> Entity.getPDCData(key: String, dataType: PersistentDataType<P?, C?>): C? =
    getData(getContainer(this), key, dataType)

fun <P, C> Block.getPDCData(key: String, dataType: PersistentDataType<P?, C?>): C? =
    getData(getContainer(this), key, dataType)

private fun <P, C> getData(
    container: PersistentDataContainer,
    key: String,
    dataType: PersistentDataType<P?, C?>
): C? = container.get(createKey(key), dataType)

private fun createKey(key: String): NamespacedKey = NamespacedKey(Initializer.getPlugin(), key)

fun ItemStack.copyPDCTo(to: Entity) {
    getContainer(this).copyTo(getContainer(to), true)
}

fun ItemStack.copyPDCTo(to: Block) {
    copyTo(getContainer(this), getContainer(to))
}

fun Block.copyPDCTo(to: ItemStack) {
    copyTo(getContainer(this), getContainer(to))
}

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
