package me.captainpotatoaim.myplugin.custom_items.custom_fuse_tnt

import me.captainpotatoaim.myplugin.custom_items.CustomItem
import me.captainpotatoaim.myplugin.util.copyPDCTo
import me.captainpotatoaim.myplugin.util.getPDCData
import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.persistence.PersistentDataType
import kotlin.math.roundToInt

class TNTListener : Listener {

    @EventHandler
    fun onTNTPlace(event: BlockPlaceEvent) {
        val itemInHand = event.itemInHand
        val blockPlaced = event.blockPlaced

        if (CustomItem.isOfType(itemInHand, CustomFuseTNT::class.java)) {
            itemInHand.copyPDCTo(blockPlaced)
        }
    }

    @EventHandler
    fun onTNTBreak(event: BlockBreakEvent) {
        val block = event.block
        if (!CustomItem.isOfType(block, CustomFuseTNT::class.java)) return

        val blockLocation = block.location
        event.isDropItems = false
        val fuseSeconds = block.getPDCData("fuse_seconds", PersistentDataType.FLOAT)!!

        if (event.player.gameMode == GameMode.CREATIVE) return
        val drop = CustomFuseTNT.getItem(1, fuseSeconds)
        blockLocation.world.dropItemNaturally(blockLocation, drop)
    }

    @EventHandler
    fun onTntSpawn(event: EntitySpawnEvent) {
        val tntEntity = event.entity as? TNTPrimed ?: return
        val block: Block = tntEntity.location.block
        if (!CustomItem.isOfType(block, CustomFuseTNT::class.java)) return

        val seconds = block.getPDCData("fuse_seconds", PersistentDataType.FLOAT)!!
        tntEntity.fuseTicks = (seconds * 20.0).roundToInt()
    }
}