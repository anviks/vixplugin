package me.captainpotatoaim.myplugin.enderman

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.HashSet
import java.util.UUID

class BecomeEnderman : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender.isOp && sender is Player) {
            endermenPlayers.add(sender.uniqueId)
        }

        return true
    }

    companion object {
        var endermenPlayers: MutableSet<UUID?> = HashSet<UUID?>()
    }
}
