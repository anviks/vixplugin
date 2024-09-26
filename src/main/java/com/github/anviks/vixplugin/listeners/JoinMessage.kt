package com.github.anviks.vixplugin.listeners

import com.github.anviks.vixplugin.util.PDCManager.getPDCData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.HashMap
import java.util.Objects
import java.util.UUID

class JoinMessage(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.getPlayer()
        val vanished = player.getPDCData<Byte?, Boolean?>("vanished", PersistentDataType.BOOLEAN)

        if (vanished == true) {
            event.joinMessage(null)
        } else {
            event.joinMessage(
                Objects.requireNonNull<Component?>(event.joinMessage()).append(Component.text(", tell them to leave"))
            )
        }

        if (!permissions.containsKey(player.uniqueId)) {
            val attachment = player.addAttachment(plugin)
            permissions.put(player.uniqueId, attachment)
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player = event.getPlayer()
        val vanished = player.getPDCData<Byte?, Boolean?>("vanished", PersistentDataType.BOOLEAN)

        if (vanished == true) {
            event.quitMessage(null)
        } else {
            event.quitMessage(
                Component.text("Good! ", NamedTextColor.YELLOW)
                    .append(Objects.requireNonNull<Component?>(event.quitMessage()))
            )
        }
    }

    companion object {
        @JvmField
        var permissions: HashMap<UUID?, PermissionAttachment?> = HashMap<UUID?, PermissionAttachment?>()
    }
}
