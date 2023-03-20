package me.captainpotatoaim.myplugin.listeners;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.Permission;
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
}
