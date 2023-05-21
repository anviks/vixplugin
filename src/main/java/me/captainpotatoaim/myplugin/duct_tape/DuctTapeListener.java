package me.captainpotatoaim.myplugin.duct_tape;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Random;

public class DuctTapeListener implements Listener {
    final int UNICODE_DIFFERENCE = 'm' - 'f';
    final Random randomizer = new Random();

    @EventHandler
    public void onChatUse(AsyncPlayerChatEvent event) {
        if (DuctTape.tapedPlayers.contains(event.getPlayer().getUniqueId())) {
            String message = event.getMessage();
            StringBuilder newMessage = new StringBuilder();

            for (char letter : message.toCharArray()) {
                if (letter == ' ') {
                    newMessage.append(' ');
                } else {
                    newMessage.append((char) ('f' + UNICODE_DIFFERENCE * randomizer.nextInt(2)));
                }
            }

            event.setMessage(newMessage.toString());
        }
    }
}
