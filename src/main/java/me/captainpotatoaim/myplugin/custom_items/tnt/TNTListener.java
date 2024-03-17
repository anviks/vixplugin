package me.captainpotatoaim.myplugin.custom_items.tnt;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class TNTListener implements Listener {
    Map<Location, Integer> placedTNTs = new HashMap<>();
    Set<UUID> activeTNTs = new HashSet<>();

    @EventHandler
    public void onTNTPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        Block blockPlaced = event.getBlockPlaced();

        if (blockPlaced.getType() == Material.TNT && Tagger.hasIdentifier(itemInHand, CustomFuseTNT.IDENTIFIER)) {
            double seconds = Double.parseDouble(itemInHand.getItemMeta()
                    .getLore().get(0)
                    .replaceFirst("Fuse time: ", "")
                    .replaceFirst(" seconds", ""));
            placedTNTs.put(blockPlaced.getLocation(), (int) Math.round(seconds * 20));
        }
    }

    @EventHandler
    public void onTNTBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.TNT) {
            return;
        }

        Location blockLocation = block.getLocation();
        if (!placedTNTs.containsKey(blockLocation)) {
            return;
        }

        event.setDropItems(false);
        double fuseSeconds = placedTNTs.get(blockLocation) / 20.0;
        ItemStack drop = CustomFuseTNT.create(1, fuseSeconds);
        blockLocation.getWorld().dropItemNaturally(blockLocation, drop);

        placedTNTs.remove(blockLocation);
    }

    @EventHandler
    public void onTNTLight(EntitySpawnEvent event) {
        if (event.getEntity().getType() == EntityType.PRIMED_TNT) {
            Location litLocation = event.getLocation().subtract(0.5, 0, 0.5);
            if (placedTNTs.containsKey(litLocation)) {
                TNTPrimed tnt = (TNTPrimed) event.getEntity();
                tnt.setFuseTicks(placedTNTs.get(litLocation));
                placedTNTs.remove(litLocation);

            }
        }
    }
}
