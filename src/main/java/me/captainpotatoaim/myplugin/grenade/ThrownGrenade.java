package me.captainpotatoaim.myplugin.grenade;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ThrownGrenade implements Listener {

    public int taskId = 0;

    @EventHandler
    public void onItemDropped(PlayerDropItemEvent event) {

        if (event.getItemDrop().getItemStack().isSimilar(GrenadeItem.grenadeItem(1))) {
            taskId = event.getPlayer().getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Initializer.class), () -> event.getPlayer().getWorld().createExplosion(event.getItemDrop().getLocation(), 10), 100);
        }

    }

    @EventHandler
    public void onItemPicked(EntityPickupItemEvent event) {

        if (event.getItem().getItemStack().isSimilar(GrenadeItem.grenadeItem(1))) {
            event.getEntity().getServer().getScheduler().cancelTask(taskId);
        }

    }

    @EventHandler
    public void onXPBottleThrown(ExpBottleEvent event) {

        if (event.getEntity().getItem().isSimilar(GrenadeItem.grenadeItem(1))) {
            event.setExperience(0);
            event.setShowEffect(false);
            event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 5);
        }

    }

}
