package me.captainpotatoaim.myplugin.duct_tape

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.HashSet
import java.util.UUID

class DuctTape : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        tapedPlayers.add(sender.server.getPlayer(args!![0])?.uniqueId)

        return true
    }

    companion object {
        @JvmField
        var tapedPlayers: HashSet<UUID?> = HashSet<UUID?>()
    }
}
