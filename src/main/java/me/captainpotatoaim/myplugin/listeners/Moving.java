package me.captainpotatoaim.myplugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Moving implements Listener {

    @EventHandler
    public void canMove(PlayerMoveEvent event) {
        if (!event.getPlayer().hasPermission("vix.move")) {
            event.setCancelled(true);
        }
    }

}
