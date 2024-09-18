package me.captainpotatoaim.myplugin.custom_items.custom_fuse_tnt;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class TNTListener implements Listener {

    @EventHandler
    public void onTNTPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        Block blockPlaced = event.getBlockPlaced();

        if (CustomItem.isOfType(itemInHand, CustomFuseTNT.class)) {
            CustomItem.copyCustomData(itemInHand, blockPlaced);
        }
    }

    @EventHandler
    public void onTNTBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!CustomItem.isOfType(block, CustomFuseTNT.class)) return;

        Location blockLocation = block.getLocation();
        event.setDropItems(false);
        float fuseSeconds = CustomItem.getData(block, "fuse_seconds", PersistentDataType.FLOAT);

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        ItemStack drop = CustomFuseTNT.getItem(1, fuseSeconds);
        blockLocation.getWorld().dropItemNaturally(blockLocation, drop);
    }

    @EventHandler
    public void onTntSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed tntEntity)) return;
        Block block = tntEntity.getLocation().getBlock();
        if (!CustomItem.isOfType(block, CustomFuseTNT.class)) return;

        float seconds = CustomItem.getData(block, "fuse_seconds", PersistentDataType.FLOAT);
        tntEntity.setFuseTicks((int) Math.round(seconds * 20.0));
    }
}
