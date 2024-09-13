package me.captainpotatoaim.myplugin.custom_items.teleport_arrows;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class TeleportArrowLand implements Listener {

    @EventHandler
    public void onArrowShot(EntityShootBowEvent event) {
        if (TeleportArrow.tpArrow(1).isSimilar(event.getConsumable())) {
            CustomItem.setType(event.getProjectile(), TeleportArrow.class);
        }
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }

        if (arrow.getPersistentDataContainer().equals(TeleportArrow.tpArrow(1).getItemMeta().getPersistentDataContainer())) {
            event.setCancelled(true);
            Player player = (Player) arrow.getShooter();
            assert player != null;
            Vector direction = player.getEyeLocation().getDirection();
            player.teleport(event.getEntity().getLocation().setDirection(direction));
            event.getEntity().remove();
        }
    }
}
