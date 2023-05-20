package me.captainpotatoaim.myplugin.teleport_arrows;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.explosive_arrows.ExplosiveArrow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

public class TpArrowLand implements Listener {

    public static FixedMetadataValue teleportValue = new FixedMetadataValue(JavaPlugin.getPlugin(Initializer.class),
            "864684fd68h4d6fh4d6j4g6f84jj68k4");

    @EventHandler
    public void onArrowShot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getInventory().containsAtLeast(TeleportArrow.tpArrow(1), 1)) {
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack == null) {
                        continue;
                    }
                    if ((itemStack.getType() == Material.ARROW ||
                            itemStack.getType() == Material.SPECTRAL_ARROW ||
                            itemStack.getType() == Material.TIPPED_ARROW) &&
                            !itemStack.isSimilar(TeleportArrow.tpArrow(1))) {
                        break;
                    } else if (itemStack.isSimilar(TeleportArrow.tpArrow(1))) {
                        event.getProjectile().setMetadata("vd68f4sd6f846846",
                                teleportValue);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {

        if (event.getEntity() instanceof Arrow arrow) {
            if (arrow.getMetadata("vd68f4sd6f846846").contains(teleportValue)) {
                Player player = (Player) arrow.getShooter();
                assert player != null;
                Vector direction = player.getEyeLocation().getDirection();
                player.teleport(event.getEntity().getLocation().setDirection(direction));
                event.getEntity().remove();
            }
        }

    }




}
