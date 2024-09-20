package me.captainpotatoaim.myplugin.custom_items.custom_fuse_tnt

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import me.captainpotatoaim.myplugin.util.PDCManager
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class CustomFuseTNT : CustomItem() {

    override fun getItem(count: Int): ItemStack {
        return getItem(count, 8f)
    }

    companion object {
        fun getItem(count: Int, fuseSeconds: Float): ItemStack {
            val item = ItemStack.of(Material.TNT, count)
            val meta = checkNotNull(item.itemMeta)
            meta.lore(
                listOf(
                    text("Fuse time: $fuseSeconds seconds")
                )
            )
            item.setItemMeta(meta)
            setType(item, CustomFuseTNT::class.java)
            PDCManager.setData(item, "fuse_seconds", PersistentDataType.FLOAT, fuseSeconds)

            return item
        }
    }
}