package com.github.anviks.vixplugin.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class InventoryCommand(private val plugin: JavaPlugin) : CustomCommand {

    override fun register() {
        CommandAPICommand("inventory")
            .withAliases("inv")
            .withPermission("vixplugin.commands.moderator")
            .withArguments(EntitySelectorArgument.OnePlayer("target"))
            .executesPlayer(this::run)
            .register(plugin)
    }

    private fun run(sender: Player, arguments: CommandArguments) {
        val target = arguments.get("target") as Player
        sender.openInventory(target.inventory)
    }
}
