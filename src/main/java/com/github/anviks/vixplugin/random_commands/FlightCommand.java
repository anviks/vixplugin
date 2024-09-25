package com.github.anviks.vixplugin.random_commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class FlightCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            Player target;

            if (sender instanceof Player && args.length == 0) {
                target = (Player) sender;
                target.setAllowFlight(!target.getAllowFlight());
                target.setFlying(target.getAllowFlight());
            } else if (args.length == 0) {
                sender.sendMessage(text("You need to specify a player.", RED));
                return true;
            } else {
                target = sender.getServer().getPlayerExact(args[0]);
                if (target != null) {
                    target.setAllowFlight(!target.getAllowFlight());
                    target.setFlying(target.getAllowFlight());
                } else {
                    sender.sendMessage(text("No such player found.", RED));
                    return true;
                }
            }

            if (target.getAllowFlight()) {
                target.sendMessage(text("Flying enabled.", GREEN));
                if (target != sender) {
                    sender.sendMessage(
                            text("Enabled flying for ")
                                    .append(target.displayName())
                                    .append(text("."))
                                    .color(GREEN)
                    );
                }
            } else {
                target.sendMessage(text("Flying disabled.", GREEN));
                if (target != sender) {
                    sender.sendMessage(
                            text("Disabled flying for ")
                                    .append(target.displayName())
                                    .append(text("."))
                                    .color(GREEN)
                    );
                }
            }

        } else {
            sender.sendMessage(text("YOU are only allowed to fly with elytra.", RED));
            return true;
        }

        return true;
    }
}
