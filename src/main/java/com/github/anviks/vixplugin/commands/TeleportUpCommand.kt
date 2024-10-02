package com.github.anviks.vixplugin.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TeleportUpCommand(private val plugin: JavaPlugin) : CustomCommand {

    override fun register() {
        val baseCommand = CommandAPICommand("teleport-up")
            .withAliases("tp-up")

        baseCommand.copy()
            .withPermission("vixplugin.commands.utility")
            .executesPlayer(this::run)
            .register(plugin)

        baseCommand
            .withPermission("vixplugin.commands.fun")
            .withArguments(EntitySelectorArgument.ManyEntities("targets"))
            .executes(this::run)
            .register(plugin)
    }

    private fun run(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Entity>>("targets") ?: listOf(sender as Player)
        targets.forEach { it.teleport(getRandomLocationAbove(it)) }
        sender.sendMessage(text("Teleported ${targets.size} entities up by a random distance.", GREEN))
    }

    private fun getRandomLocationAbove(entity: Entity): Location {
        val entityLocation = entity.location
        val height = entityLocation.y
        val maxHeight = entity.world.maxHeight.toDouble()
        val destinationHeight = Math.random() * (maxHeight - height)
        entityLocation.y = height + destinationHeight

        return entityLocation
    }
}
