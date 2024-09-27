package com.github.anviks.vixplugin.random_commands.protect_area

import com.github.anviks.vixplugin.Area
import com.github.anviks.vixplugin.CustomCommand
import com.github.anviks.vixplugin.PluginState
import com.github.anviks.vixplugin.SerializableLocation
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class ProtectArea(
    private val plugin: JavaPlugin,
    private val pluginState: PluginState,
) : CustomCommand {

    override fun register() {
        CommandAPICommand("protect-area")
            .withPermission(CommandPermission.OP)
            .withArguments(LocationArgument("start", LocationType.BLOCK_POSITION))
            .withArguments(LocationArgument("end", LocationType.BLOCK_POSITION))
            .withArguments(LiteralArgument("as"))
            .withArguments(GreedyStringArgument("area-name"))
            .executes(this::run)
            .register(plugin)
    }

    private fun run(sender: CommandSender, arguments: CommandArguments) {
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
}
