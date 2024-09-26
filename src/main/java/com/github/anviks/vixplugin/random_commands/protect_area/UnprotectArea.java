package com.github.anviks.vixplugin.random_commands.protect_area;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.anviks.vixplugin.random_commands.protect_area.ProtectedAreas.getProtectedAreas;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class UnprotectArea implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(RED + "noob");
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        if (getProtectedAreas().remove(args[0]) == null) {
            sender.sendMessage(RED + "No such area");
        } else {
            sender.sendMessage(GREEN + "Successfully removed defenses from " + args[0]);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return args.length == 1 ? getProtectedAreas().keySet().stream().toList() : List.of();
    }
}
