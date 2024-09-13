package me.captainpotatoaim.myplugin.custom_items.explosive_arrows;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public class ExplosiveArrowLand implements Listener {
    private boolean dispenserShotExplosiveArrow = false;

    @EventHandler
    public void onDispenserPowered(BlockDispenseEvent event) {
        if (CustomItem.isOfType(event.getItem(), ExplosiveArrow.class)) {
            dispenserShotExplosiveArrow = true;
        }
    }

    @EventHandler
    public void onBowArrowShot(EntityShootBowEvent event) {
        ItemStack consumable = event.getConsumable();
        Entity projectile = event.getProjectile();

        if (consumable != null && CustomItem.isOfType(consumable, ExplosiveArrow.class)) {
            CustomItem.setType(projectile, ExplosiveArrow.class);
        }
    }

    @EventHandler
    public void onArrowShot(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if (dispenserShotExplosiveArrow && shooter instanceof BlockProjectileSource) {
            dispenserShotExplosiveArrow = false;
            CustomItem.setType(projectile, ExplosiveArrow.class);
        }
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (CustomItem.isOfType(projectile, ExplosiveArrow.class)) {
            projectile.getWorld().createExplosion(projectile.getLocation(), 7, false, true, projectile);
            projectile.remove();
        }
    }
}
