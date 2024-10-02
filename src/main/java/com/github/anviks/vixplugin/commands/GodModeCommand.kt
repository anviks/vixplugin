package com.github.anviks.vixplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class GodMode implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            Player target;

            if (sender instanceof Player && args.length == 0) {
                target = (Player) sender;
                target.setInvulnerable(!target.isInvulnerable());

            } else if (args.length == 0) {
                sender.sendMessage(text("You need to specify a player.", RED));
                return true;
            } else {
                target = sender.getServer().getPlayerExact(args[0]);
                if (target != null) {
                    target.setInvulnerable(!target.isInvulnerable());
                } else {
                    sender.sendMessage(text("No such player found.", RED));
                    return true;
                }
            }

            if (target.isInvulnerable()) {
                target.sendMessage(text("You are now in god mode.", GREEN));
                if (target != sender) {
                    sender.sendMessage(target.displayName().append(text(" is now in god mode.")).color(GREEN));
                }
            } else {
                target.sendMessage(text("You are no longer in god mode.", GREEN));
                if (target != sender) {
                    sender.sendMessage(target.displayName().append(text(" is no longer in god mode.")).color(GREEN));
                }
            }
        } else {
            sender.sendMessage(text("You lack the divinity to use this command.", RED));
            return true;
        }

        return true;
    }
}
