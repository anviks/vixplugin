package me.captainpotatoaim.myplugin.enderman;

import me.captainpotatoaim.myplugin.custom_items.teleport_arrows.TeleportArrow;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.*;

public class ArrowListener implements Listener {

    @EventHandler
    void onArrowHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (!(projectile instanceof AbstractArrow)) return;

        if (projectile.getPersistentDataContainer()
                .equals(TeleportArrow.tpArrow(1)
                        .getItemMeta()
                        .getPersistentDataContainer())) return;

        Entity hitEntity = event.getHitEntity();

        if (hitEntity == null) return;

        if (BecomeEnderman.endermenPlayers.contains(hitEntity.getUniqueId())) {
            Location hitEntityLocation = hitEntity.getLocation();
            Set<Location> availableLocations = new HashSet<>();

            // Find a suitable location to teleport the player to
            for (int x = -32; x <= 32; x++) {
                for (int y = -32; y <= 32; y++) {
                    innerCoordinateLoop:
                    for (int z = -32; z <= 32; z++) {
                        Location destination = hitEntityLocation.clone();
                        destination.setX(Math.floor(destination.getX()) + 0.5);
                        destination.setY(Math.floor(destination.getY()));
                        destination.setZ(Math.floor(destination.getZ()) + 0.5);
                        destination.add(x, y, z);

                        for (int i = 0; i < 3; i++) {
                            Block block = destination.add(0, 1, 0).getBlock();
                            if (!block.isPassable() || block.isLiquid()) {
                                continue innerCoordinateLoop;
                            }
                        }

                        destination.subtract(0, 3, 0);
                        Block block = destination.getBlock();
                        if (block.isLiquid() || block.isPassable()) {
                            continue;
                        }

                        BlockData blockData = block.getBlockData();

                        if (blockData instanceof Waterlogged waterlogged
                                && waterlogged.isWaterlogged()) {
                            continue;
                        }

                        availableLocations.add(destination);
                    }
                }
            }

            Random random = new Random();
            Location destination = availableLocations.stream().toList().get(random.nextInt(0, availableLocations.size()));

            World world = hitEntity.getWorld();
            world.playSound(hitEntity, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            world.spawnParticle(Particle.PORTAL, hitEntityLocation, 200, 0.3, 0.3, 0.3);
            hitEntity.teleport(destination.add(0, 1, 0));
            event.setCancelled(true);
        }
    }
}
