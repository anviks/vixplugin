package me.captainpotatoaim.myplugin.custom_items.railgun;

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import me.captainpotatoaim.myplugin.util.AdventureHelper.createAlternatingColoredText
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.NamedTextColor.YELLOW
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack


class Railgun : CustomItem() {
    override fun getItem(count: Int): ItemStack {
        val railGun = ItemStack(Material.TRIDENT, 1)
        val railGunMeta = checkNotNull(railGun.itemMeta)
        railGunMeta.displayName(createAlternatingColoredText("RAILGUN", GRAY, YELLOW))
        railGunMeta.addEnchant(Enchantment.INFINITY, 1, true)
        railGun.setItemMeta(railGunMeta)
        setType(railGun, Railgun::class.java)

        return railGun
    }
}
