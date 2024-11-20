package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.configuration.ConfigDependent
import com.github.anviks.vixplugin.configuration.ConfigOption
import org.bukkit.inventory.ItemStack

@ConfigDependent(ConfigOption.CUSTOM_ITEMS_ENABLED)
interface CustomItem {
    fun getItem(count: Int): ItemStack
}
