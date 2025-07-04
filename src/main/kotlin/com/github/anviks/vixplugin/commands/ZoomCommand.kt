package com.github.anviks.vixplugin.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

class ZoomCommand(private val plugin: Plugin) : CustomCommand {

    private val runningCommands = mutableSetOf<UUID>()

    override fun getCommands(): List<CommandAPICommand> {
        return listOf(
            CommandAPICommand("zoom")
                .withPermission("vixplugin.commands.fun")
                .executesPlayer(this::run)
        )
    }

    private fun run(sender: Player, args: CommandArguments) {
        if (sender.uniqueId in runningCommands) {
            sender.sendMessage(text("You are already zooming!", RED))
            return
        }

        val previousGameMode = sender.previousGameMode
        val currentGameMode = sender.gameMode

        sender.velocity = sender.eyeLocation.direction.multiply(100)
        sender.playSound(sender.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        sender.gameMode = GameMode.SPECTATOR

        runningCommands.add(sender.uniqueId)

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (previousGameMode != null) sender.gameMode = previousGameMode  // To preserve history
            sender.gameMode = currentGameMode
            runningCommands.remove(sender.uniqueId)
            sender.setGravity(true)
        }, 20)
    }
}
