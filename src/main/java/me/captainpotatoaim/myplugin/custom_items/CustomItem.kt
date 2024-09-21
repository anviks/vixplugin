package me.captainpotatoaim.myplugin.custom_items

import me.captainpotatoaim.myplugin.util.PDCManager
import me.captainpotatoaim.myplugin.util.StringHelper.camelCaseToSnakeCase
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
                is ItemStack -> PDCManager.getData(obj, CUSTOM_ITEM_KEY, PersistentDataType.STRING)
                is Entity -> PDCManager.getData(obj, CUSTOM_ITEM_KEY, PersistentDataType.STRING)
                is Block -> PDCManager.getData(obj, CUSTOM_ITEM_KEY, PersistentDataType.STRING)
                else -> throw IllegalArgumentException("Unsupported type: " + obj.javaClass)
            }

            return data.isPresent && data.get() == identifier
        }

        fun setType(item: ItemStack, clazz: Class<*>) {
            PDCManager.setData(item, CUSTOM_ITEM_KEY, PersistentDataType.STRING, classToIdentifier(clazz))
        }

        fun setType(entity: Entity?, clazz: Class<*>) {
            PDCManager.setData(entity, CUSTOM_ITEM_KEY, PersistentDataType.STRING, classToIdentifier(clazz))
        }

        private fun classToIdentifier(clazz: Class<*>): String {
            return camelCaseToSnakeCase(clazz.getSimpleName())
        }
    }
}
