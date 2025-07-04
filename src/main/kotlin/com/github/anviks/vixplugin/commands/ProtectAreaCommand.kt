package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.Area
import com.github.anviks.vixplugin.PluginState
import com.github.anviks.vixplugin.SerializableLocation
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityExplodeEvent

class ProtectAreaCommand(private val pluginState: PluginState) : CustomCommand {

    override fun getCommands(): List<CommandAPICommand> {
        val protectCommand = CommandAPICommand("protect-area")
            .withPermission("vixplugin.commands.utility")
            .withArguments(LocationArgument("start", LocationType.BLOCK_POSITION))
            .withArguments(LocationArgument("end", LocationType.BLOCK_POSITION))
            .withArguments(LiteralArgument("as"))
            .withArguments(GreedyStringArgument("area-name"))
            .executes(this::protectArea)

        val unprotectCommand = CommandAPICommand("unprotect-area")
            .withPermission(CommandPermission.OP)
            .withArguments(
                GreedyStringArgument("area-name")
                    .replaceSuggestions(ArgumentSuggestions.strings { pluginState.protectedAreas.keys.toTypedArray() })
            )
            .executes(this::unprotectArea)

        return listOf(protectCommand, unprotectCommand)
    }

    private fun protectArea(sender: CommandSender, arguments: CommandArguments) {
        val start = arguments.get("start") as Location
        val end = arguments.get("end") as Location
        val areaName = arguments.get("area-name") as String

        if (pluginState.protectedAreas.containsKey(areaName)) {
            sender.sendMessage(text("Area with name \"$areaName\" already exists", RED))
            return
        }

        val area = Area(SerializableLocation(start), SerializableLocation(end))
        pluginState.protectedAreas[areaName] = area
        sender.sendMessage(text("Successfully protected area \"$areaName\"", GREEN))
    }

    private fun unprotectArea(sender: CommandSender, arguments: CommandArguments) {
        val areaName = arguments.get("area-name") as String
        if (pluginState.protectedAreas.remove(areaName) == null) {
            sender.sendMessage(text("Area with name \"$areaName\" does not exist", RED))
        } else {
            sender.sendMessage(text("Successfully removed defenses from \"$areaName\"", GREEN))
        }
    }

    @EventHandler
    fun blockDamaged(event: BlockDamageEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockBroken(event: BlockBreakEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockExplosion(event: BlockExplodeEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockPlaced(event: BlockPlaceEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockMultiPlaced(event: BlockMultiPlaceEvent) {
        cancelIfProtected(event)
    }

    @EventHandler
    fun blockExplosionByEntity(event: EntityExplodeEvent) {
        event.blockList().removeIf { isProtected(it.location) }
    }

    private fun <T> cancelIfProtected(event: T) where T : BlockEvent, T : Cancellable {
        val location = event.block.location

        if (isProtected(location)) {
            event.isCancelled = true
        }
    }

    private fun isProtected(location: Location): Boolean {
        for (area in pluginState.protectedAreas.values) {
            if (location in area) {
                return true
            }
        }

        return false
    }
}
