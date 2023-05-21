package me.captainpotatoaim.myplugin.explosive_arrows;

import me.captainpotatoaim.myplugin.util.Inventory;
import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public class ExplosiveArrowLand implements Listener {

    private static boolean dispenserShotExplosiveArrow = false;

    @EventHandler
    public void onDispenserPowered(BlockDispenseEvent event) {
        if (event.getItem().isSimilar(ExplosiveArrow.getExplosiveArrow(1))) {
            dispenserShotExplosiveArrow = true;
        }
    }

    @EventHandler
    public void onArrowShot(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if (projectile.getType() != EntityType.SPECTRAL_ARROW) {
            return;
        }

        if (shooter instanceof Player player) {
            if (Inventory.isShootableArrow(ExplosiveArrow.getExplosiveArrow(1), player)) {
                Tagger.tagEntity(projectile, ExplosiveArrow.identifier);
            }
        } else if (shooter instanceof BlockProjectileSource && dispenserShotExplosiveArrow) {
            Tagger.tagEntity(projectile, ExplosiveArrow.identifier);
            dispenserShotExplosiveArrow = false;
        }

    }


    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof SpectralArrow arrow) {
            var itemContainer = arrow.getPersistentDataContainer();
            var entityContainer = ExplosiveArrow.getExplosiveArrow(1).getItemMeta().getPersistentDataContainer();
            if (itemContainer.equals(entityContainer)) {
                arrow.getWorld().createExplosion(arrow.getLocation(), 7);
                arrow.remove();
            }
        }

    }
}
