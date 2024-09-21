package me.captainpotatoaim.myplugin.vanish

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.executors.CommandArguments
import me.captainpotatoaim.myplugin.CustomCommand
import me.captainpotatoaim.myplugin.Initializer
import me.captainpotatoaim.myplugin.util.PDCManager
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class Vanish : CustomCommand, Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val isVanished = PDCManager.getData(player, "vanished", PersistentDataType.BOOLEAN)

        if (isVanished == true) {
            this.updatePlayerVisibilityForAll(player, false)
        }

        for (onlinePlayer in Bukkit.getOnlinePlayers().minus(player)) {
            val isSomeoneElseVanished = PDCManager.getData(onlinePlayer, "vanished", PersistentDataType.BOOLEAN)

            if (isSomeoneElseVanished == true) {
                this.updatePlayerVisibility(onlinePlayer, player, false)
            }
        }
    }

    override fun register(plugin: JavaPlugin) {
        CommandAPICommand("vanish")
            .withPermission(CommandPermission.OP)
            .withArguments(LiteralArgument("silently"))
            .executesPlayer(this::runSilently)
            .register(plugin)

        CommandAPICommand("vanish")
            .withPermission(CommandPermission.OP)
            .withArguments(LiteralArgument("with-effects"))
            .executesPlayer(this::runWithEffects)
            .register(plugin)
    }

    private fun runSilently(player: Player, args: CommandArguments) {
        val isVanished = PDCManager.getData(player, "vanished", PersistentDataType.BOOLEAN)

        if (isVanished == true) {
            this.updatePlayerVisibilityForAll(player, true)
            this.broadcastJoinEvent(player)
        } else {
            this.broadcastQuitEvent(player)
            this.updatePlayerVisibilityForAll(player, false)
        }
    }

    private fun runWithEffects(player: Player, args: CommandArguments) {
        val isVanished = PDCManager.getData(player, "vanished", PersistentDataType.BOOLEAN)

        if (isVanished == true) {
            var delay = 0L
            val scheduler = Bukkit.getScheduler()

            val neighbourOffsets = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to 1, 1 to 1, 1 to 0, 1 to -1, 0 to -1)

            for (offset in neighbourOffsets) {
                scheduler.runTaskLater(
                    Initializer.getPlugin(),
                    Runnable { spawnLightningWithOffset(player.location, offset) },
                    delay
                )
                delay += 5
            }

            delay += 20

            scheduler.runTaskLater(Initializer.getPlugin(), Runnable {
                for (offset in neighbourOffsets) {
                    spawnLightningWithOffset(player.location, offset)
                }

                this.updatePlayerVisibilityForAll(player, true)
                this.broadcastJoinEvent(player)
            }, delay)
        } else {
            this.broadcastQuitEvent(player)
            this.updatePlayerVisibilityForAll(player, false)
            this.spawnVanishSpecialEffects(player)
        }
    }

    private fun spawnLightningWithOffset(location: Location, offset: Pair<Int, Int>) {
        location.y -= 1
        location.x += offset.first
        location.z += offset.second
        location.world.strikeLightningEffect(location)
    }

    private fun spawnVanishSpecialEffects(player: Player) {
        val location = player.location
        location.y += 1
        player.world.spawnParticle(Particle.LARGE_SMOKE, location, 250, 0.5, 0.5, 0.5, 0.1)
    }

    private fun broadcastJoinEvent(player: Player) {
        val joinEvent = PlayerJoinEvent(
            player,
            player.displayName().append(text(" joined the game")).color(NamedTextColor.YELLOW)
        )
        Bukkit.getPluginManager().callEvent(joinEvent)
        joinEvent.joinMessage()?.let { Bukkit.broadcast(it) }
    }

    private fun broadcastQuitEvent(player: Player) {
        val quitEvent = PlayerQuitEvent(
            player,
            player.displayName().append(text(" left the game")).color(NamedTextColor.YELLOW),
            PlayerQuitEvent.QuitReason.DISCONNECTED
        )
        Bukkit.getPluginManager().callEvent(quitEvent)
        quitEvent.quitMessage()?.let { Bukkit.broadcast(it) }
    }

    private fun updatePlayerVisibilityForAll(player: Player, show: Boolean) {
        PDCManager.setData(player, "vanished", PersistentDataType.BOOLEAN, !show)

        for (onlinePlayer in Bukkit.getOnlinePlayers().minus(player)) {
            updatePlayerVisibility(player, onlinePlayer, show)
        }
    }

    private fun updatePlayerVisibility(target: Player, observer: Player, show: Boolean) {
        val plugin = Initializer.getPlugin()
        val packet = if (show) getTabListAddPacket(target) else getTabListRemovePacket(target)

        val nmsObserver = (observer as CraftPlayer).handle
        nmsObserver.connection.sendPacket(packet)

        if (show) {
            observer.showPlayer(plugin, target)
        } else {
            observer.hidePlayer(plugin, target)
        }
    }

    private fun getTabListRemovePacket(player: Player): ClientboundPlayerInfoRemovePacket {
        return ClientboundPlayerInfoRemovePacket(listOf(player.uniqueId))
    }

    private fun getTabListAddPacket(player: Player): ClientboundPlayerInfoUpdatePacket {
        val nmsPlayer = (player as CraftPlayer).handle

        val actions = EnumSet.of(
            ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
            ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED
        )

        return ClientboundPlayerInfoUpdatePacket(actions, listOf(nmsPlayer))
    }
}