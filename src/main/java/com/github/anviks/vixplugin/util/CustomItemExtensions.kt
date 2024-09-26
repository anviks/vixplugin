package com.github.anviks.vixplugin.util

import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

private const val CUSTOM_ITEM_KEY = "custom_item_type"

fun ItemStack.isOfCustomType(clazz: Class<*>): Boolean = isObjectOfCustomType(this, clazz)
fun Entity.isOfCustomType(clazz: Class<*>): Boolean = isObjectOfCustomType(this, clazz)
fun Block.isOfCustomType(clazz: Class<*>): Boolean = isObjectOfCustomType(this, clazz)

fun ItemStack.setCustomType(clazz: Class<*>) = setObjectCustomType(this, clazz)
fun Entity.setCustomType(clazz: Class<*>) = setObjectCustomType(this, clazz)

private fun isObjectOfCustomType(obj: Any, clazz: Class<*>): Boolean {
    val identifier = classToIdentifier(clazz)

    val data = when (obj) {
        is ItemStack -> obj.getPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING)
        is Entity -> obj.getPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING)
        is Block -> obj.getPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING)
        else -> throw IllegalArgumentException("Unsupported type: " + obj.javaClass)
    }

    return data == identifier
}

private fun setObjectCustomType(obj: Any, clazz: Class<*>) {
    val identifier = classToIdentifier(clazz)

    when (obj) {
        is ItemStack -> obj.setPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING, identifier)
        is Entity -> obj.setPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING, identifier)
        else -> throw IllegalArgumentException("Unsupported type: " + obj.javaClass)
    }
}

private fun classToIdentifier(clazz: Class<*>): String {
    return clazz.simpleName.camelToSnake()
}
