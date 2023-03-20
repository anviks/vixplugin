package me.captainpotatoaim.myplugin.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class Moving implements Listener {

    @EventHandler
    public void canMove(PlayerMoveEvent event) {
        if (!event.getPlayer().hasPermission("vix.move")) {
            event.setCancelled(true);
        }
    }

}
