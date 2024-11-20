package com.github.anviks.vixplugin.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class EnderChestCommand(private val plugin: JavaPlugin) : CustomCommand {

    override fun register() {
        val baseCommand = CommandAPICommand("ender-chest")
            .withAliases("echest", "ec")

        baseCommand.copy()
            .withPermission("vixplugin.commands.moderator")
            .withArguments(EntitySelectorArgument.OnePlayer("target"))
            .executesPlayer(this::run)
            .register(plugin)

        baseCommand
            .withPermission("vixplugin.commands.utility")
            .executesPlayer(this::run)
            .register(plugin)
    }

    private fun run(sender: Player, arguments: CommandArguments) {
        val target = arguments.getOrDefault("target", sender) as Player
        sender.openInventory(target.enderChest)
    }
}
