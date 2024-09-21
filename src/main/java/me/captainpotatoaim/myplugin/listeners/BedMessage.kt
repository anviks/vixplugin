package me.captainpotatoaim.myplugin.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class BedMessage implements Listener {

    @EventHandler
    public void onPlayerWake(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        Server server = player.getServer();
        if (player.getPlayerTime() == 24000) {
            server.broadcast(Component.text(player.displayName() + " slept through the night. Thanks!"));
        } else {
            int random = (int) (Math.random() * 2);
            switch (random) {
                case 0 -> player.sendMessage("Why did you wake up?");
                case 1 -> player.sendMessage("Are you having trouble sleeping?");
            }
        }
    }
}
