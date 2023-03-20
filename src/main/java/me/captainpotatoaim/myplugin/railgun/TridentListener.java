package me.captainpotatoaim.myplugin.railgun;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

public class TridentListener implements Listener {

    @EventHandler
    public void onTridentThrown(final ProjectileLaunchEvent event) {

        if (event.getEntity().getType() == EntityType.TRIDENT && event.getEntity().getShooter() instanceof Player player) {

            if (player.getInventory().getItemInMainHand().isSimilar(RailGunItem.create())) {
                event.setCancelled(true);
            }

            Vector shot = player.getEyeLocation().getDirection();
            Location explosion = player.getLocation();
            boolean invulnerable = player.isInvulnerable();

            if (!invulnerable) {
                player.setInvulnerable(true);
            }

            explosion = explosion.add(shot.multiply(1.8));
            for (int i = 0; i < 400; i++) {
                explosion = explosion.add(shot);
                player.getWorld().createExplosion(explosion, 2.6f);
            }

            if (!invulnerable) {
                player.setInvulnerable(false);
            }

        }

    }


}
