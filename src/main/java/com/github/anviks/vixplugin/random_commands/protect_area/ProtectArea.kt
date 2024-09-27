package com.github.anviks.vixplugin.random_commands.protect_area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.anviks.vixplugin.random_commands.protect_area.ProtectedAreas.getProtectedAreas;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ProtectArea implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(RED + "noob");
            return true;
        }

        if (args.length < 8) {
            return false;
        }

        if (getProtectedAreas().containsKey(args[7])) {
            sender.sendMessage(YELLOW + "An area with the name of \"" + args[7] + "\" already exists");
            return true;
        }

        Player player = sender instanceof Player ? (Player) sender : null;

        World world = player == null
                ? sender.getServer().getWorld("world")
                : player.getWorld();

        assert world != null;

        Location senderLocation = player == null
                ? world.getSpawnLocation()
                : player.getLocation();

        List<String> locationStrings = Arrays.asList(args).subList(0, 6);
        List<Integer> coords = new ArrayList<>();
        List<Integer> senderCoords = new ArrayList<>(
                List.of(
                        senderLocation.getBlockX(),
                        senderLocation.getBlockY(),
                        senderLocation.getBlockZ()
                )
        );

        for (int i = 0; i < locationStrings.size(); i++) {
            String loc = locationStrings.get(i);
            int coordinate;

            if (loc.equals("~")) {
                coordinate = senderCoords.get(i % 3);
            } else {
                try {
                    coordinate = Integer.parseInt(loc);
                } catch (NumberFormatException e) {
                    sender.sendMessage(RED + "Expected integer");
                    return false;
                }
            }

            coords.add(coordinate);
        }

        var from = new SerializableLocation(world.getName(), coords.get(0), coords.get(1), coords.get(2));
        var to = new SerializableLocation(world.getName(), coords.get(3), coords.get(4), coords.get(5));

        Area area = new Area(from, to);
        getProtectedAreas().put(args[7], area);
        sender.sendMessage(GREEN + "Successfully protected area " + args[7]);

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp() || !(sender instanceof Player player)) {
            return null;
        }

        Block facingBlock = player.getTargetBlockExact(5);
        String facingX, facingY, facingZ;

        if (facingBlock != null) {
            Location location = facingBlock.getLocation();
            facingX = String.valueOf(location.getBlockX());
            facingY = String.valueOf(location.getBlockY());
            facingZ = String.valueOf(location.getBlockZ());
        } else {
            facingX = "~";
            facingY = "~";
            facingZ = "~";
        }


        switch (args.length) {
            case 1, 4 -> {
                return List.of(
                        "%s".formatted(facingX),
                        "%s %s".formatted(facingX, facingY),
                        "%s %s %s".formatted(facingX, facingY, facingZ)
                );
            }

            case 2, 5 -> {
                return List.of(
                        "%s".formatted(facingY),
                        "%s %s".formatted(facingY, facingZ)
                );
            }

            case 3, 6 -> {
                return List.of(
                        "%s".formatted(facingZ)
                );
            }

            case 7 -> {
                return List.of("as");
            }

            default -> {
                return List.of();
            }
        }
    }
}
