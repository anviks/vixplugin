package com.github.anviks.vixplugin.commands

import dev.jorel.commandapi.CommandAPICommand

interface CustomCommand {
    fun getCommands(): List<CommandAPICommand>
}
