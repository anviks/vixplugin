package me.captainpotatoaim.myplugin.custom_items.railgun;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class TridentListener implements Listener {

    @EventHandler
    public void onTridentThrown(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof LivingEntity shooter)) {
            return;
        }

        EntityEquipment equipment = shooter.getEquipment();
        if (equipment == null) {
            return;
        }

        ItemStack itemInMainHand = equipment.getItemInMainHand();
        ItemStack itemInOffHand = equipment.getItemInOffHand();

        ItemStack shotTrident = itemInMainHand.getType() == Material.TRIDENT
                ? itemInMainHand
                : itemInOffHand;

        if (event.getEntityType() != EntityType.TRIDENT) {
            return;
        }

        String shotTridentIdentifier = shotTrident.getItemMeta().getPersistentDataContainer().get(Tagger.KEY, PersistentDataType.STRING);
        if (!Railgun.IDENTIFIER.equals(shotTridentIdentifier)) {
            return;
        }

        event.setCancelled(true);
        Vector shot = shooter.getEyeLocation().getDirection();
        Location explosion = shooter.getLocation();

        explosion = explosion.add(shot.multiply(1.8)).add(0, 2, 0);
        for (int i = 0; i < 400; i++) {
            explosion = explosion.add(shot);
            shooter.getWorld().createExplosion(explosion, 2.6f, true, true, shooter);
        }
    }
}
