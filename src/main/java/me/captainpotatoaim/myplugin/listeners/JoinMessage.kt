package me.captainpotatoaim.myplugin.listeners;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.util.PDCManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class JoinMessage implements Listener {

    public static HashMap<UUID, PermissionAttachment> permissions = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Optional<Boolean> vanished = PDCManager.getData(player, "vanished", PersistentDataType.BOOLEAN);

        if (vanished.isPresent() && vanished.get()) {
            event.joinMessage(null);
        } else {
            event.joinMessage(Objects.requireNonNull(event.joinMessage()).append(text(", tell them to leave")));
        }

        if (!permissions.containsKey(player.getUniqueId())) {
            PermissionAttachment attachment = player.addAttachment(Initializer.getPlugin());
            permissions.put(player.getUniqueId(), attachment);
        }
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<Boolean> vanished = PDCManager.getData(player, "vanished", PersistentDataType.BOOLEAN);

        if (vanished.isPresent() && vanished.get()) {
            event.quitMessage(null);
        } else {
            event.quitMessage(text("Good! ", YELLOW).append(Objects.requireNonNull(event.quitMessage())));
        }
    }
}
