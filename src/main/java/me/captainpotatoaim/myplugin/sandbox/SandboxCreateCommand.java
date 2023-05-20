package me.captainpotatoaim.myplugin.sandbox;

import com.mojang.brigadier.CommandDispatcher;
import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class SandboxCreateCommand {

    public static HashMap<Integer, World> worlds = new HashMap<>();
    public static long previousCreationTime = 0;
    public static int maxSandboxes = JavaPlugin.getPlugin(Initializer.class).getConfig().getInt("max-sandboxes", 10);

    public static boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (System.currentTimeMillis() - previousCreationTime < 30_000) {
            sender.sendMessage(ChatColor.RED + "You need to wait " +
                    (30 - (System.currentTimeMillis() - previousCreationTime) / 1000) +
                    " more seconds before using that command, as a sandbox was just created.");
            return true;
        }

        int slot;

        switch (args.length) {

            case 1 -> {
                WorldCreator creator = new WorldCreator("sandbox-" + (int) (Math.random() * 1_000_000_000));
                creator.environment(World.Environment.NORMAL);
                creator.type(WorldType.FLAT);
                creator.generateStructures(false);
                creator.generatorSettings("{\"layers\": " +
                        "[{\"block\": \"bedrock\", \"height\": 1}, " +
                        "{\"block\": \"sandstone\", \"height\": 120}], " +
                        "\"biome\":\"desert\"}");
                slot = addSandbox(creator, sender);

            }

            case 2 -> {
                WorldCreator creator = new WorldCreator("sandbox-" + (int) (Math.random() * 1_000_000_000));
                World.Environment environment = null;
                switch (args[1]) {
                    case "overworld" -> environment = World.Environment.NORMAL;
                    case "nether" -> environment = World.Environment.NETHER;
                    case "end" -> environment = World.Environment.THE_END;
                    default -> {
                        return false;
                    }
                }
                creator.environment(environment);
                creator.type(WorldType.NORMAL);
                slot = addSandbox(creator, sender);
            }

            default -> {
                sender.sendMessage(ChatColor.RED + "Sandbox wasn't created.");
                return false;
            }

        }

        if (slot == 0) {
            sender.sendMessage(ChatColor.YELLOW +
                    "I can't believe you created" + maxSandboxes + "sandboxes. " +
                    "I will not allow you to create any more (unless you edit the config.yml, of course).");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Creating sandbox...");
        }

//        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
//
//        dispatcher.register(
//                literal("foo")
//                        .then(
//                                argument("bar", integer())
//                                        .executes(c -> {
//                                            System.out.println("Bar is " + getInteger(c, "bar"));
//                                            return 1;
//                                        })
//                        )
//                        .executes(c -> {
//                            System.out.println("Called foo with no arguments");
//                            return 1;
//                        })
//        );

        return true;

    }

    public static int addSandbox(WorldCreator creator, CommandSender sender) {

        for (int i = 1; i <= maxSandboxes; i++) {
            if (!worlds.containsKey(i)) {
                previousCreationTime = System.currentTimeMillis();
                int slot = i;
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Initializer.class), () -> {
                    World world = creator.createWorld();
                    world.setTime(1000);
                    world.setGameRule(GameRule.KEEP_INVENTORY, true);
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                    worlds.put(slot, world);
                    sender.sendMessage(ChatColor.GREEN + worlds.get(slot).getName() + " created in slot " + slot);
                }, 1);
                return slot;
            }
        }

        return 0;
    }


}
