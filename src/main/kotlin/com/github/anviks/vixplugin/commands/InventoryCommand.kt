package com.github.anviks.vixplugin.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.entity.Player

class InventoryCommand : CustomCommand {

    override fun getCommands(): List<CommandAPICommand> {
        return listOf(
            CommandAPICommand("inventory")
                .withAliases("inv")
                .withPermission("vixplugin.commands.moderator")
                .withArguments(EntitySelectorArgument.OnePlayer("target"))
                .executesPlayer(this::run)
        )
    }

    private fun run(sender: Player, arguments: CommandArguments) {
        val target = arguments.get("target") as Player
        sender.openInventory(target.inventory)
    }
}
