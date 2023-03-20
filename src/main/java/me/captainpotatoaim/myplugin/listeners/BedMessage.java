package me.captainpotatoaim.myplugin.listeners;

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
            server.broadcastMessage(String.format("%s slept through the night. Thanks!", player.getDisplayName()));
        } else {
            int random = (int) (Math.random() * 2);
            switch (random) {
                case 0 -> player.sendMessage("Why did you wake up?");
                case 1 -> player.sendMessage("Are you having trouble sleeping?");
            }
        }
    }

}
