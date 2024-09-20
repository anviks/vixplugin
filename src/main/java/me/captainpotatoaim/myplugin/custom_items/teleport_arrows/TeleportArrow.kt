package me.captainpotatoaim.myplugin.custom_items.teleport_arrows

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

class TeleportArrow : CustomItem() {

    override fun getItem(count: Int): ItemStack {
        val tpArrow = ItemStack(Material.ARROW, count)
        val tpArrowMeta = checkNotNull(tpArrow.itemMeta)
        tpArrowMeta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true)
        tpArrowMeta.displayName(Component.text("Teleport arrow", NamedTextColor.DARK_AQUA))
        tpArrow.setItemMeta(tpArrowMeta)
        setType(tpArrow, TeleportArrow::class.java)

        return tpArrow
    }
}