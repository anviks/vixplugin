package me.captainpotatoaim.myplugin.custom_items.rapid_fire_bow

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class RapidFireBow : CustomItem() {

    override fun getItem(count: Int): ItemStack {
        val bow = ItemStack(Material.BOW)
        val meta = checkNotNull(bow.itemMeta)
        bow.setItemMeta(meta)
        // TODO: Customise item
        setType(bow, RapidFireBow::class.java)

        return bow
    }
}