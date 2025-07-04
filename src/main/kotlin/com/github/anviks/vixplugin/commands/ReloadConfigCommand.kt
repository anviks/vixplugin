package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.configuration.ConfigManager
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.command.CommandSender

class ReloadConfigCommand(private val configManager: ConfigManager) : CustomCommand {

    override fun getCommands(): List<CommandAPICommand> {
        return listOf(
            CommandAPICommand("reloadconfig")
                .withPermission("vixplugin.reloadconfig")
                .executes(this::run)
        )
    }

    private fun run(sender: CommandSender, arguments: CommandArguments) {
        configManager.reloadConfig()
    }
}