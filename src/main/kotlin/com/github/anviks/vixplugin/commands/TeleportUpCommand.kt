package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.util.withOverloads
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class TeleportUpCommand : CustomCommand {

    override fun getCommands(): List<CommandAPICommand> {
        return CommandAPICommand("teleport-up")
            .withAliases("tp-up")
            .withOverloads(
                {
                    it.withPermission("vixplugin.commands.utility")
                        .executesPlayer(this::run)
                },
                {
                    it.withPermission("vixplugin.commands.fun")
                        .withArguments(EntitySelectorArgument.ManyEntities("targets"))
                        .executes(this::run)
                }
            )
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
