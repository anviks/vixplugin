package me.captainpotatoaim.myplugin.custom_items.grappling_hook

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class GrapplingHook : CustomItem() {

    override fun getItem(count: Int): ItemStack {
        val hook = ItemStack(Material.FISHING_ROD, count)
        val meta = checkNotNull(hook.itemMeta)
        meta.displayName(Component.text("Grappling hook"))
        meta.isUnbreakable = true
        hook.setItemMeta(meta)
        setType(hook, GrapplingHook::class.java)

        return hook
    }
}