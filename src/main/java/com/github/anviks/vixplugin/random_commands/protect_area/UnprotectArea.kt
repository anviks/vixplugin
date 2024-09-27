package com.github.anviks.vixplugin.random_commands.protect_area

import com.github.anviks.vixplugin.CustomCommand
import com.github.anviks.vixplugin.ProtectedAreas
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class UnprotectArea(
    private val plugin: JavaPlugin,
    private val protectedAreas: ProtectedAreas,
) : CustomCommand {

    override fun register() {
        CommandAPICommand("unprotect-area")
            .withPermission(CommandPermission.OP)
            .withArguments(
                GreedyStringArgument("area-name")
                    .replaceSuggestions(ArgumentSuggestions.strings { protectedAreas.keys.toTypedArray() })
            )
            .executes(this::run)
            .register(plugin)
    }

    private fun run(sender: CommandSender, arguments: CommandArguments) {
        val areaName = arguments.get("area-name") as String
        if (protectedAreas.remove(areaName) == null) {
            sender.sendMessage(text("Area with name \"$areaName\" does not exist", RED))
        } else {
            sender.sendMessage(text("Successfully removed defenses from \"$areaName\"", GREEN))
        }
    }
}
