package me.captainpotatoaim.myplugin.random_commands.protect_area;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.Buffer;
import java.util.*;
import java.util.stream.Collectors;

import static me.captainpotatoaim.myplugin.random_commands.protect_area.ProtectedAreas.FILE_PATH;
import static me.captainpotatoaim.myplugin.random_commands.protect_area.ProtectedAreas.protectedAreas;
import static org.bukkit.ChatColor.*;

public class ProtectArea implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(RED + "noob");
            return true;
        }

        if (args.length < 8) {
            return false;
        }

        if (protectedAreas.containsKey(args[7])) {
            sender.sendMessage(YELLOW + "An area with the name of \"" + args[7] + "\" already exists");
            return true;
        }

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

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
                ));

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

        Location from = new Location(world, coords.get(0), coords.get(1), coords.get(2));
        Location to = new Location(world, coords.get(3), coords.get(4), coords.get(5));

        Area area = new Area(from, to);
        protectedAreas.put(args[7], area);
        Bukkit.broadcastMessage(protectedAreas.toString());

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
