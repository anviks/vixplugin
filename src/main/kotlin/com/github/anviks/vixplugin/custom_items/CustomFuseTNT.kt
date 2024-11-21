package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.util.PDCManager.copyPDCTo
import com.github.anviks.vixplugin.util.PDCManager.getPDCData
import com.github.anviks.vixplugin.util.PDCManager.setPDCData
import com.github.anviks.vixplugin.util.isOfCustomType
import com.github.anviks.vixplugin.util.setCustomType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.TNTPrimeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

class CustomFuseTNT(private val plugin: Plugin) : CustomItem, Listener {

    override fun getItem(count: Int): ItemStack {
        return getItem(count, 4 * 20)
    }

    fun getItem(count: Int, fuseTicks: Int): ItemStack {
        val item = ItemStack(Material.TNT, count)
        item.setCustomType<CustomFuseTNT>()
        item.setFuseTicks(fuseTicks)

        return item
    }

    private fun updateLore(itemStack: ItemStack, lore: List<Component>) {
        val meta = itemStack.itemMeta
        meta.lore(lore)
        itemStack.itemMeta = meta
    }

    private fun ItemStack.getFuseTicks(): Int =
        this.getPDCData("fuse_ticks", PersistentDataType.INTEGER)
            ?: throw IllegalArgumentException("ItemStack does not have fuse_seconds data")

    private fun Block.getFuseTicks(): Int =
        this.getPDCData("fuse_ticks", PersistentDataType.INTEGER)
            ?: throw IllegalArgumentException("Block does not have fuse_seconds data")

    private fun ItemStack.setFuseTicks(ticks: Int) {
        updateLore(
            this, listOf(
                text("Fuse time: ${ticks / 20f} seconds")
            )
        )
        this.setPDCData("fuse_ticks", PersistentDataType.INTEGER, ticks)
    }

    private fun Block.setFuseTicks(ticks: Int) {
        this.setPDCData("fuse_ticks", PersistentDataType.INTEGER, ticks)
    }

    @EventHandler
    fun onTNTPlace(event: BlockPlaceEvent) {
        val itemInHand = event.itemInHand
        val blockPlaced = event.blockPlaced

        if (itemInHand.isOfCustomType<CustomFuseTNT>()) {
            itemInHand.copyPDCTo(blockPlaced)
        }
    }

    @EventHandler
    fun onTNTBreak(event: BlockBreakEvent) {
        val block = event.block
        if (!block.isOfCustomType<CustomFuseTNT>()) return

        val blockLocation = block.location
        event.isDropItems = false
        val fuseSeconds = block.getFuseTicks()

        if (event.player.gameMode == GameMode.CREATIVE) return
        val drop = getItem(1, fuseSeconds)
        blockLocation.world.dropItemNaturally(blockLocation, drop)
    }

    /**
     * Randomize the fuse time of the TNT, when it's ignited by an explosion.
     * The fuse time will be between 25% and 75% of the original fuse time.
     * This ratio is taken from vanilla TNT, that normally has a fuse time of 80 ticks,
     * but when ignited by an explosion, it has a fuse time of 20 to 60 ticks.
     */
    @EventHandler
    fun onTNTExplode(event: EntityExplodeEvent) {
        for (block in event.blockList()) {
            if (!block.isOfCustomType<CustomFuseTNT>()) continue

            var ticks = block.getFuseTicks()
            val randomMultiplier = Random.nextDouble(.25, .75)
            ticks = (ticks * randomMultiplier).roundToInt()
            block.setFuseTicks(ticks)
        }
    }

    @EventHandler
    fun onTNTSpawn(event: TNTPrimeEvent) {
//        Due to a bug in CustomBlockData, event.block's PDC is always empty,
//        when ignited by an explosion
//        println(CustomBlockData(event.block, plugin).keys)
    }

    @EventHandler
    fun onTNTSpawn(event: EntitySpawnEvent) {
        val tntEntity = event.entity as? TNTPrimed ?: return
        val block = tntEntity.location.block
        if (!block.isOfCustomType<CustomFuseTNT>()) return
        tntEntity.fuseTicks = block.getFuseTicks()
    }

    @EventHandler
    fun onAnvilUse(event: PrepareAnvilEvent) {
        val first = event.inventory.firstItem
        val second = event.inventory.secondItem

        if (first?.isOfCustomType<CustomFuseTNT>() != true) return

        val ticks = first.getFuseTicks()
        val result = first.clone()

        if (second?.type == Material.STRING) {
            result.setFuseTicks(ticks + second.amount)
        } else if (second?.type == Material.FLINT) {
            result.setFuseTicks(max(ticks - second.amount, 0))
        } else {
            return
        }

        event.result = result
        event.inventory.repairCost = 1
    }
}