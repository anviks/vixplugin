package me.captainpotatoaim.myplugin.sandbox;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SandboxLeaveCommand {

    public static boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        Player player = Bukkit.getServer().getPlayerExact(args[1]);
        SandboxPlayerData data = SandboxJoinCommand.sandboxedPlayers.get(player.getUniqueId());
        data.revertPlayerState();
        SandboxJoinCommand.sandboxedPlayers.remove(player.getUniqueId());
        player.sendMessage(Initializer.defaultWorlds.toString());
        sender.getServer().broadcastMessage(player + SandboxJoinCommand.sandboxedPlayers.toString());

        // TODO: Leave all

        return true;
    }
}
