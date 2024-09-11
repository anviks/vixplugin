package me.captainpotatoaim.myplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class JoinMessage implements Listener {

    public static HashMap<UUID, PermissionAttachment> permissions = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(player.getDisplayName() + " has joined the server. Tell them to fuck off!");
//        if (!permissions.containsKey(player.getUniqueId())) {
//            PermissionAttachment attachment = player.addAttachment(JavaPlugin.getPlugin(Initializer.class));
//            permissions.put(player.getUniqueId(), attachment);
//        }
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage("Good! " + event.getQuitMessage());
    }

    @EventHandler
    void oooo(AsyncPlayerPreLoginEvent event) {

    }
}
