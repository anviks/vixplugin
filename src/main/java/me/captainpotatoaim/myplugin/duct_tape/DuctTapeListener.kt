package me.captainpotatoaim.myplugin.duct_tape

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.lang.StringBuilder
import java.util.Random

class DuctTapeListener : Listener {

    @EventHandler
    fun onChatUse(event: AsyncChatEvent) {
        if (DuctTape.tapedPlayers.contains(event.getPlayer().uniqueId)) {
            val message = (event.message() as TextComponent).content()
            val newMessage = StringBuilder()

            for (letter in message.toCharArray()) {
                if (letter == ' ') {
                    newMessage.append(' ')
                } else {
                    newMessage.append(('f'.code + UNICODE_DIFFERENCE * randomizer.nextInt(2)).toChar())
                }
            }

            event.message(Component.text(newMessage.toString()))
        }
    }

    companion object {
        private val UNICODE_DIFFERENCE = 'm'.code - 'f'.code
        private val randomizer = Random()
    }
}
