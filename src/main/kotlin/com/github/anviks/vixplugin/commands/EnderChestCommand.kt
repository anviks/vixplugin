package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.util.withOverloads
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.entity.Player

class EnderChestCommand : CustomCommand {

    override fun getCommands(): List<CommandAPICommand> {
        return CommandAPICommand("ender-chest")
            .withAliases("echest", "ec")
            .withOverloads(
                {
                    it.withPermission("vixplugin.commands.moderator")
                        .withArguments(EntitySelectorArgument.OnePlayer("target"))
                        .executesPlayer(this::run)
                },
                {
                    it.withPermission("vixplugin.commands.utility")
                        .executesPlayer(this::run)
                }
            )
    }

    private fun run(sender: Player, arguments: CommandArguments) {
        val target = arguments.getOrDefault("target", sender) as Player
        sender.openInventory(target.enderChest)
    }
}
