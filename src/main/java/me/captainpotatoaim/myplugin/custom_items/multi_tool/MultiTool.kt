package me.captainpotatoaim.myplugin.custom_items.multi_tool

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

class MultiTool : CustomItem() {

    override fun getItem(count: Int): ItemStack {
        val tool = ItemStack(Material.DIAMOND_PICKAXE)
        val meta = checkNotNull(tool.itemMeta)
        meta.displayName(Component.text("Multi-tool"))
        meta.lore(listOf(Component.text("One tool to fit all your needs.")))
        meta.addEnchant(Enchantment.EFFICIENCY, 5, false)
        meta.addEnchant(Enchantment.MENDING, 1, false)
        meta.addEnchant(Enchantment.UNBREAKING, 3, false)
        tool.setItemMeta(meta)
        setType(tool, MultiTool::class.java)

        return tool
    }
}