package me.captainpotatoaim.myplugin.explosive_arrows;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.BlockProjectileSource;

public class ExplosiveArrowLand implements Listener {

    public static boolean dispenserShotExplosiveArrow = false;
    public static FixedMetadataValue explosiveValue = new FixedMetadataValue(JavaPlugin.getPlugin(Initializer.class),
            "0293238950uewjfds932094u23j90djew9hondlksf");

    @EventHandler
    public void onDispenserPowered(BlockDispenseEvent event) {
        if (event.getItem().isSimilar(ExplosiveArrow.explosiveArrow(1))) {
            dispenserShotExplosiveArrow = true;
        }

    }

    @EventHandler
    public void onArrowShot(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            if (player.getInventory().containsAtLeast(ExplosiveArrow.explosiveArrow(1), 1)) {
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack == null) {
                        continue;
                    }
                    if ((itemStack.getType() == Material.ARROW
                            || itemStack.getType() == Material.SPECTRAL_ARROW
                            || itemStack.getType() == Material.TIPPED_ARROW)
                            && !itemStack.isSimilar(ExplosiveArrow.explosiveArrow(1))) {
                        break;
                    } else if (itemStack.isSimilar(ExplosiveArrow.explosiveArrow(1))) {
                        event.getEntity().setMetadata(
                                "paj32pjdfjps09232290jfs",
                                explosiveValue
                        );
                        break;
                    }
                }
            }
        } else if (event.getEntity().getShooter() instanceof BlockProjectileSource && dispenserShotExplosiveArrow) {
            event.getEntity().setMetadata("paj32pjdfjps09232290jfs",
                    explosiveValue);
            dispenserShotExplosiveArrow = false;
        }
    }


    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {

        if (event.getEntity() instanceof SpectralArrow arrow) {
            if (arrow.getMetadata("paj32pjdfjps09232290jfs")
                    .contains(explosiveValue)) {
                arrow.getWorld().createExplosion(arrow.getLocation(), 7);
                arrow.remove();
            }
        }

    }

}
