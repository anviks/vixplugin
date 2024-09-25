package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.util.camelToSnake
import com.github.anviks.vixplugin.util.getPDCData
import com.github.anviks.vixplugin.util.setPDCData
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class CustomItem {

    abstract fun getItem(count: Int): ItemStack

    companion object {

        private const val CUSTOM_ITEM_KEY = "custom_item_type"

        fun isOfType(item: ItemStack, clazz: Class<*>): Boolean = holderIsOfType(item, clazz)
        fun isOfType(entity: Entity, clazz: Class<*>): Boolean = holderIsOfType(entity, clazz)
        fun isOfType(block: Block, clazz: Class<*>): Boolean = holderIsOfType(block, clazz)

        private fun holderIsOfType(obj: Any, clazz: Class<*>): Boolean {
            val identifier = classToIdentifier(clazz)

            val data = when (obj) {
                is ItemStack -> obj.getPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING)
                is Entity -> obj.getPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING)
                is Block -> obj.getPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING)
                else -> throw IllegalArgumentException("Unsupported type: " + obj.javaClass)
            }

            return data == identifier
        }

        fun setType(item: ItemStack, clazz: Class<*>) {
            item.setPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING, classToIdentifier(clazz))
        }

        fun setType(entity: Entity, clazz: Class<*>) {
            entity.setPDCData(CUSTOM_ITEM_KEY, PersistentDataType.STRING, classToIdentifier(clazz))
        }

        private fun classToIdentifier(clazz: Class<*>): String {
            return clazz.simpleName.camelToSnake()
        }
    }
}
