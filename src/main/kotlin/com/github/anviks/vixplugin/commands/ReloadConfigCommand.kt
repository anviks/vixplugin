package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.configuration.ConfigManager
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class ReloadConfigCommand(
    private val plugin: JavaPlugin,
    private val configManager: ConfigManager,
) : CustomCommand {

    override fun register() {
        CommandAPICommand("reloadconfig")
            .withPermission("vixplugin.reloadconfig")
            .executes(this::run)
            .register(plugin)
    }

    private fun run(sender: CommandSender, arguments: CommandArguments) {
        configManager.reloadConfig()
    }
}