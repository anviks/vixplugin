package me.captainpotatoaim.myplugin.railgun;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class TridentListener implements Listener {

    @EventHandler
    public void onTridentThrown(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        if (event.getEntity().getType() != EntityType.TRIDENT) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack itemInMainHand = inventory.getItemInMainHand();
        ItemStack itemInOffHand = inventory.getItemInOffHand();

        ItemStack shotTrident = itemInMainHand.getType() == Material.TRIDENT
                ? itemInMainHand
                : itemInOffHand;

        if (!shotTrident.isSimilar(Railgun.getRailgun())) {
            return;
        }

        event.setCancelled(true);
        Vector shot = player.getEyeLocation().getDirection();
        Location explosion = player.getLocation();
        boolean invulnerable = player.isInvulnerable();

        if (!invulnerable) {
            player.setInvulnerable(true);
        }

        explosion = explosion.add(shot.multiply(1.8)).add(0, 2, 0);
        for (int i = 0; i < 400; i++) {
            explosion = explosion.add(shot);
            player.getWorld().createExplosion(explosion, 2.6f, true, true, player);
        }

        if (!invulnerable) {
            player.setInvulnerable(false);
        }
    }
}
