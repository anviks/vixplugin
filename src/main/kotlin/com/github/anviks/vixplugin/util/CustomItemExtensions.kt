package com.github.anviks.vixplugin.util

import com.github.anviks.vixplugin.util.PDCManager.getPDCData
import com.github.anviks.vixplugin.util.PDCManager.setPDCData
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

private const val CUSTOM_ITEM_KEY = "custom_item_type"

fun <T> ItemStack.isOfCustomType(clazz: Class<T>): Boolean = isObjectOfCustomType(this, clazz)
fun <T> Entity.isOfCustomType(clazz: Class<T>): Boolean = isObjectOfCustomType(this, clazz)
fun <T> Block.isOfCustomType(clazz: Class<T>): Boolean = isObjectOfCustomType(this, clazz)

inline fun <reified T> ItemStack.isOfCustomType(): Boolean = this.isOfCustomType(T::class.java)
inline fun <reified T> Entity.isOfCustomType(): Boolean = this.isOfCustomType(T::class.java)
inline fun <reified T> Block.isOfCustomType(): Boolean = this.isOfCustomType(T::class.java)

fun <T> ItemStack.setCustomType(clazz: Class<T>) = setObjectCustomType(this, clazz)
fun <T> Entity.setCustomType(clazz: Class<T>) = setObjectCustomType(this, clazz)

inline fun <reified T> ItemStack.setCustomType() = this.setCustomType(T::class.java)
inline fun <reified T> Entity.setCustomType() = this.setCustomType(T::class.java)

private fun <T> isObjectOfCustomType(obj: Any, clazz: Class<T>): Boolean {
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
