package me.captainpotatoaim.myplugin.util

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

/**
 * MC 1.21 introduced a new tag for arrows shot in creative mode. That tag gets added at the moment of shooting
 * the arrow, making comparisons with it fail. This method removes the tag from the item.
 */
fun ItemStack.copyWithoutIntangibleTag(): ItemStack {
    val unwantedTag = "minecraft:intangible_projectile=\\{}"
    val replacementPattern = String.format("(?<=\\[)%s,?|,%s", unwantedTag, unwantedTag)
    val arrowMeta = this.itemMeta

    // Example - [minecraft:enchantments={levels: {"minecraft:efficiency": 2}},minecraft:intangible_projectile={}]
    var componentString = arrowMeta.asComponentString
    // Example - [minecraft:enchantments={levels: {"minecraft:efficiency": 2}}]
    componentString = componentString.replaceFirst(replacementPattern.toRegex(), "")
    // Example - minecraft:arrow
    val itemTypeKey = this.type.key.toString()
    // Example - minecraft:arrow[minecraft:enchantments={levels: {"minecraft:efficiency": 2}}]
    val itemAsString = itemTypeKey + componentString

    return Bukkit.getItemFactory().createItemStack(itemAsString)
}