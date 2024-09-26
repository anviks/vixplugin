package com.github.anviks.vixplugin.custom_items

import org.bukkit.inventory.ShapedRecipe

interface CustomCraftableItem : CustomItem {
    fun getRecipe(): ShapedRecipe
}