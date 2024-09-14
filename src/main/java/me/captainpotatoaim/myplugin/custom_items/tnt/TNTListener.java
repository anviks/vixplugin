package me.captainpotatoaim.myplugin.custom_items.tnt;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TNTListener implements Listener {
    Map<Location, Integer> placedTNTs = new HashMap<>();

    @EventHandler
    public void onTNTPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        Block blockPlaced = event.getBlockPlaced();

        if (CustomItem.isOfType(itemInHand, CustomFuseTNT.class)) {
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
        ItemStack drop = CustomFuseTNT.getItem(1, fuseSeconds);
        blockLocation.getWorld().dropItemNaturally(blockLocation, drop);

        placedTNTs.remove(blockLocation);
    }

    @EventHandler
    public void onTNTLight(EntitySpawnEvent event) {
        if (event.getEntity().getType() == EntityType.TNT) {
            Location litLocation = event.getLocation().subtract(0.5, 0, 0.5);
            if (placedTNTs.containsKey(litLocation)) {
                TNTPrimed tnt = (TNTPrimed) event.getEntity();
                tnt.setFuseTicks(placedTNTs.get(litLocation));
                placedTNTs.remove(litLocation);

//                BukkitRunnable runnable = new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        if (tnt.isInWater()) {
//                            Location tntLocation = tnt.getLocation();
//                            Block blockAt = tnt.getWorld().getBlockAt(tntLocation);
//                            if (blockAt.getType() == Material.TNT) {
//                                blockAt.setType(Material.AIR);
//                            } else {
//                                blockAt.setType(Material.TNT);
//                            }
//                            placedTNTs.add(tntLocation);
//                            tnt.remove();
//                            cancel();
//                        }
//                    }
//                };
//
//                runnable.runTaskTimer(Initializer.plugin, 0, 1);
            }
        }
    }
}
