package me.captainpotatoaim.myplugin.duct_tape;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Random;

public class DuctTapeListener implements Listener {
    final int UNICODE_DIFFERENCE = 'm' - 'f';
    final Random randomizer = new Random();

    @EventHandler
    public void onChatUse(AsyncChatEvent event) {
        if (DuctTape.tapedPlayers.contains(event.getPlayer().getUniqueId())) {
            String message = ((TextComponent) event.message()).content();
            StringBuilder newMessage = new StringBuilder();

            for (char letter : message.toCharArray()) {
                if (letter == ' ') {
                    newMessage.append(' ');
                } else {
                    newMessage.append((char) ('f' + UNICODE_DIFFERENCE * randomizer.nextInt(2)));
                }
            }

            event.message(Component.text(newMessage.toString()));
        }
    }
}
