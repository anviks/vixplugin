package com.github.anviks.vixplugin.random_commands.protect_area;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;

public class BlockListener implements Listener {
    @EventHandler
    void blockDamaged(BlockDamageEvent event) {
        cancelIfProtected(event);
    }

    @EventHandler
    void blockBroken(BlockBreakEvent event) {
        cancelIfProtected(event);
    }

    @EventHandler
    void blockExplosion(BlockExplodeEvent event) {
        cancelIfProtected(event);
    }

    @EventHandler
    void blockExplosionByEntity(EntityExplodeEvent event) {
        var blocks = event.blockList();
        var blocksClone = new ArrayList<>(blocks);

        for (Block b : blocksClone) {
            Location location = b.getLocation();
            if (ProtectedAreas.isProtected(location)) {
                blocks.remove(b);
            }
        }
    }

    @EventHandler
    void blockPlaced(BlockPlaceEvent event) {
        cancelIfProtected(event);
    }

    @EventHandler
    void blockMultiPlaced(BlockMultiPlaceEvent event) {
        cancelIfProtected(event);
    }

    private static <T extends BlockEvent & Cancellable> void cancelIfProtected(T event) {
        Location location = event.getBlock().getLocation();

        if (ProtectedAreas.isProtected(location)) {
            event.setCancelled(true);
        }
    }
}
