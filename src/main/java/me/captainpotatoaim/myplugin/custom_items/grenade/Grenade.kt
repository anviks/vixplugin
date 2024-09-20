package me.captainpotatoaim.myplugin.custom_items.grenade

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Grenade : CustomItem() {

    override fun getItem(count: Int): ItemStack {
        val grenade = ItemStack(Material.EXPERIENCE_BOTTLE, count)
        val grenadeMeta = checkNotNull(grenade.itemMeta)
        grenadeMeta.displayName(Component.text("GRENADE", NamedTextColor.RED))
        grenadeMeta.lore(listOf(Component.text("Toss it at someone.")))
        grenade.setItemMeta(grenadeMeta)
        setType(grenade, Grenade::class.java)

        return grenade
    }
}