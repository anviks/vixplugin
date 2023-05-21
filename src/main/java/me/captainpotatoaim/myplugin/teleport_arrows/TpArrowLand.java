package me.captainpotatoaim.myplugin.teleport_arrows;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class TpArrowLand implements Listener {

    @EventHandler
    public void onArrowShot(EntityShootBowEvent event) {
        if (TeleportArrow.tpArrow(1).isSimilar(event.getConsumable())) {
            Tagger.tagEntity(event.getProjectile(), TeleportArrow.IDENTIFIER);
        }
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }

        if (arrow.getPersistentDataContainer().equals(TeleportArrow.tpArrow(1).getItemMeta().getPersistentDataContainer())) {
            Player player = (Player) arrow.getShooter();
            assert player != null;
            Vector direction = player.getEyeLocation().getDirection();
            player.teleport(event.getEntity().getLocation().setDirection(direction));
            event.getEntity().remove();
        }
    }
}
