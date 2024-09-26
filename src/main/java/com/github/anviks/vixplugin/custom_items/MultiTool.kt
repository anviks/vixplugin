package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.util.isOfCustomType
import com.github.anviks.vixplugin.util.setCustomType
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class MultiTool : CustomItem, Listener {

    override fun getItem(count: Int): ItemStack {
        val tool = ItemStack(Material.DIAMOND_PICKAXE)
        val meta = checkNotNull(tool.itemMeta)
        meta.displayName(Component.text("Multi-tool"))
        meta.lore(listOf(Component.text("One tool to fit all your needs.")))
        meta.addEnchant(Enchantment.EFFICIENCY, 5, false)
        meta.addEnchant(Enchantment.MENDING, 1, false)
        meta.addEnchant(Enchantment.UNBREAKING, 3, false)
        tool.setItemMeta(meta)
        tool.setCustomType(MultiTool::class.java)

        return tool
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val eventItem = event.item ?: return

        if (!eventItem.isOfCustomType(MultiTool::class.java)) return

        val player = event.player
        val block = event.clickedBlock

        if (event.action == Action.LEFT_CLICK_BLOCK) {
            checkNotNull(block)
            handleLeftClick(player, block, eventItem)
        }
    }

    private fun handleLeftClick(player: Player, clickedBlock: Block, tool: ItemStack) {
        val blockMaterial = clickedBlock.type
        val isNetherite = tool.type.toString().startsWith("NETHERITE_")
        val toolForBlock = getToolForBlock(blockMaterial, isNetherite)
        ensureMaterial(player, tool, toolForBlock!!)
    }

    private fun getToolForBlock(blockMaterial: Material, isNetherite: Boolean): Material? {
        if (Tag.MINEABLE_AXE.isTagged(blockMaterial)) {
            return if (isNetherite) Material.NETHERITE_AXE else Material.DIAMOND_AXE
        } else if (Tag.MINEABLE_PICKAXE.isTagged(blockMaterial)) {
            return if (isNetherite) Material.NETHERITE_PICKAXE else Material.DIAMOND_PICKAXE
        } else if (Tag.MINEABLE_SHOVEL.isTagged(blockMaterial)) {
            return if (isNetherite) Material.NETHERITE_SHOVEL else Material.DIAMOND_SHOVEL
        } else if (Tag.MINEABLE_HOE.isTagged(blockMaterial)) {
            return if (isNetherite) Material.NETHERITE_HOE else Material.DIAMOND_HOE
        }

        return null
    }

    private fun ensureMaterial(player: Player, tool: ItemStack, toolMaterial: Material) {
        if (tool.type != toolMaterial) {
            val newTool = ItemStack.of(toolMaterial)
            newTool.setItemMeta(tool.itemMeta)
            player.inventory.setItemInMainHand(newTool)
        }
    }
}