package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.util.copyPDCTo
import com.github.anviks.vixplugin.util.getPDCData
import com.github.anviks.vixplugin.util.isOfCustomType
import com.github.anviks.vixplugin.util.setCustomType
import com.github.anviks.vixplugin.util.setPDCData
import net.kyori.adventure.text.Component.text
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.roundToInt

class CustomFuseTNT : CustomCraftableItem, Listener {

    override fun getItem(count: Int): ItemStack {
        return getItem(count, 8f)
    }

    fun getItem(count: Int, fuseSeconds: Float): ItemStack {
        val item = ItemStack.of(Material.TNT, count)
        val meta = checkNotNull(item.itemMeta)
        meta.lore(
            listOf(
                text("Fuse time: $fuseSeconds seconds")
            )
        )
        item.setItemMeta(meta)
        item.setCustomType(CustomFuseTNT::class.java)
        item.setPDCData("fuse_seconds", PersistentDataType.FLOAT, fuseSeconds)

        return item
    }


    @EventHandler
    fun onTNTPlace(event: BlockPlaceEvent) {
        val itemInHand = event.itemInHand
        val blockPlaced = event.blockPlaced

        if (itemInHand.isOfCustomType(CustomFuseTNT::class.java)) {
            itemInHand.copyPDCTo(blockPlaced)
        }
    }

    @EventHandler
    fun onTNTBreak(event: BlockBreakEvent) {
        val block = event.block
        if (!block.isOfCustomType(CustomFuseTNT::class.java)) return

        val blockLocation = block.location
        event.isDropItems = false
        val fuseSeconds = block.getPDCData("fuse_seconds", PersistentDataType.FLOAT)!!

        if (event.player.gameMode == GameMode.CREATIVE) return
        val drop = getItem(1, fuseSeconds)
        blockLocation.world.dropItemNaturally(blockLocation, drop)
    }

    @EventHandler
    fun onTntSpawn(event: EntitySpawnEvent) {
        val tntEntity = event.entity as? TNTPrimed ?: return
        val block: Block = tntEntity.location.block
        if (!block.isOfCustomType(CustomFuseTNT::class.java)) return

        val seconds = block.getPDCData("fuse_seconds", PersistentDataType.FLOAT)!!
        tntEntity.fuseTicks = (seconds * 20.0).roundToInt()
    }
}