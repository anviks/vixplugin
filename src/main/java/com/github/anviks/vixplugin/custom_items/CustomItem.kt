package com.github.anviks.vixplugin.custom_items

import org.bukkit.inventory.ItemStack

interface CustomItem {
    fun getItem(count: Int): ItemStack
}
