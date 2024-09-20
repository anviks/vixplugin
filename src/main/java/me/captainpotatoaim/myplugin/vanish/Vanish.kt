package me.captainpotatoaim.myplugin.vanish

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.executors.CommandArguments
import me.captainpotatoaim.myplugin.CustomCommand
import me.captainpotatoaim.myplugin.Initializer
import me.captainpotatoaim.myplugin.util.PDCManager
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
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class Vanish : CustomCommand, Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val isVanished = PDCManager.getData(player, "vanished", PersistentDataType.BOOLEAN)

        if (isVanished.isPresent && isVanished.get()) {
            this.updatePlayerVisibilityForAll(player, false)
        }

        for (onlinePlayer in Bukkit.getOnlinePlayers().minus(player)) {
            val isSomeoneElseVanished = PDCManager.getData(onlinePlayer, "vanished", PersistentDataType.BOOLEAN)

            if (isSomeoneElseVanished.isPresent && isSomeoneElseVanished.get()) {
                this.updatePlayerVisibility(onlinePlayer, player, false)
            }
        }
    }

    override fun register(plugin: JavaPlugin) {
        CommandAPICommand("vanish")
            .withPermission(CommandPermission.OP)
            .executesPlayer(this::run)
            .register(plugin)
    }

    private fun run(player: Player, args: CommandArguments) {
        val location = player.location
        val isVanished = PDCManager.getData(player, "vanished", PersistentDataType.BOOLEAN)

        if (isVanished.isPresent && isVanished.get()) {
            val lightningLocations = ArrayList<Location>()

            for (i in -1..1) {
                for (j in -1..1) {
                    val lightningLocation = location.clone()
                    lightningLocation.y -= 1
                    lightningLocation.x += i
                    lightningLocation.z += j
                    lightningLocations.add(lightningLocation)
                }
            }

            var delay = 0
            val scheduler = Bukkit.getScheduler()

            for (i in listOf(0, 1, 2, 5, 8, 7, 6, 3)) {
                scheduler.runTaskLater(
                    Initializer.getPlugin(),
                    Runnable { player.world.strikeLightningEffect(lightningLocations[i]) }, delay.toLong()
                )
                delay += 5
            }

            delay += 15

            scheduler.runTaskLater(Initializer.getPlugin(), Runnable {
                for (lightningLocation in lightningLocations) {
                    player.world.strikeLightningEffect(lightningLocation)
                }
                this.updatePlayerVisibilityForAll(player, true)
            }, delay.toLong())
        } else {
            this.updatePlayerVisibilityForAll(player, false)
            location.y += 1
            player.world.spawnParticle(Particle.LARGE_SMOKE, location, 250, 0.5, 0.5, 0.5, 0.1)
        }
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