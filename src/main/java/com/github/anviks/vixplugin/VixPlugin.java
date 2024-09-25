package com.github.anviks.vixplugin;

import com.github.anviks.vixplugin.custom_items.*;
import com.github.anviks.vixplugin.duct_tape.DuctTape;
import com.github.anviks.vixplugin.enderman.ArrowListener;
import com.github.anviks.vixplugin.enderman.BecomeEnderman;
import com.github.anviks.vixplugin.listeners.*;
import com.github.anviks.vixplugin.random_commands.*;
import com.github.anviks.vixplugin.random_commands.protect_area.BlockListener;
import com.github.anviks.vixplugin.random_commands.protect_area.ProtectArea;
import com.github.anviks.vixplugin.random_commands.protect_area.ProtectedAreas;
import com.github.anviks.vixplugin.random_commands.protect_area.UnprotectArea;
import com.github.anviks.vixplugin.sandbox.Inventory;
import com.github.anviks.vixplugin.vanish.Vanish;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class VixPlugin extends JavaPlugin {

    private static JavaPlugin plugin;
    public static List<World> defaultWorlds = null;
    private DuctTape ductTape;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = getPlugin(VixPlugin.class);
        defaultWorlds = getServer().getWorlds();
        ProtectedAreas.loadAreas();

        registerCommands();
        registerEvents();
        registerRecipes();

        loadTapedPlayers();
    }

    private void registerCommands() {
        var commands = new HashMap<String, CommandExecutor>() {{
            put("loyalsquad", new DogCommand());
            put("slap", new SlapCommand());
            put("fly", new FlightCommand());
            put("inventory", new InventoryCommand());
            put("echest", new EnderChestCommand());
            put("launch", new LaunchCommand());
            put("zoom", new ZoomCommand());
            put("freeze", new Freeze());
            put("unfreeze", new UnFreeze());
            put("god", new GodMode());
//            put("sandbox", new SandboxMainCommand());
            put("tp-up", new TeleportUp());
            put("ender-toggle", new BecomeEnderman());
            put("protect", new ProtectArea());
            put("unprotect", new UnprotectArea());
            put("world", new ChangeWorlds());
        }};

        ExplosiveArrow explosiveArrow = new ExplosiveArrow(this);
        GrapplingHook grapplingHook = new GrapplingHook();
        Grenade grenade = new Grenade();
        MultiTool multiTool = new MultiTool();
        Railgun railgun = new Railgun();
        RapidFireBow rapidFireBow = new RapidFireBow();
        TeleportArrow teleportArrow = new TeleportArrow();
        CustomFuseTNT customFuseTNT = new CustomFuseTNT();

        CustomItem[] customItems = {
                explosiveArrow,
                grapplingHook,
                grenade,
                multiTool,
                railgun,
                rapidFireBow,
                teleportArrow,
                customFuseTNT
        };

        for (var command : commands.entrySet()) {
            PluginCommand cmd = this.getCommand(command.getKey());
            if (cmd == null) {
                throw new RuntimeException("Command " + command.getKey() + " not found");
            }
            cmd.setExecutor(command.getValue());
        }

        PluginManager pluginManager = this.getServer().getPluginManager();

        GiveCustomItem giveCustomItem = new GiveCustomItem(this, customItems);
        Vanish vanish = new Vanish(this);
        EnchantAnything enchantAnything = new EnchantAnything(this);
        PrankCommand prankCommand = new PrankCommand(this);
        ductTape = new DuctTape(this);

        giveCustomItem.register();
        vanish.register();
        enchantAnything.register();
        prankCommand.register();
        ductTape.register();

        pluginManager.registerEvents(vanish, this);
        pluginManager.registerEvents(ductTape, this);

        pluginManager.registerEvents(explosiveArrow, this);
        pluginManager.registerEvents(grapplingHook, this);
        pluginManager.registerEvents(grenade, this);
        pluginManager.registerEvents(multiTool, this);
        pluginManager.registerEvents(railgun, this);
        pluginManager.registerEvents(rapidFireBow, this);
        pluginManager.registerEvents(teleportArrow, this);
        pluginManager.registerEvents(customFuseTNT, this);
    }

    private void registerEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        var listeners = new Listener[]{
                new DeathMessages(),
                new JoinMessage(),
                new BedMessage(),
                new Moving(),
                new Inventory(),
                new ArrowListener(),
                new BlockListener(),
                new EntityListener(),
        };

        for (var listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    private void registerRecipes() {
        var recipes = new ShapedRecipe[]{
                new ExplosiveArrow(this).getRecipe(),
        };

        for (var recipe : recipes) {
            Bukkit.addRecipe(recipe);
        }
    }

    @Override
    public void onDisable() {
        ProtectedAreas.saveAreas();

        saveTapedPlayers();

//        for (Player player : getServer().getOnlinePlayers()) {
//            if (!defaultWorlds.contains(player.getWorld())) {
//                SandboxJoinCommand.sandboxedPlayers.get(player.getUniqueId()).revertPlayerState();
//            }
//        }

//        for (World world : getServer().getWorlds()) {
//            if (!defaultWorlds.contains(world)) {
//                Bukkit.unloadWorld(world, false);
//                try {
//                    FileUtils.deleteDirectory(world.getWorldFolder());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }

    }

    private void saveTapedPlayers() {
        var file = getConfig();
        ductTape.getTapedPlayers().forEach((uuid, unmuteTime) -> file.set("taped-players." + uuid, unmuteTime.toString()));
        saveConfig();
    }

    private void loadTapedPlayers() {
        var file = getConfig();
        var tapedPlayersSection = file.getConfigurationSection("taped-players");
        if (tapedPlayersSection != null) {
            for (var key : tapedPlayersSection.getKeys(false)) {
                var uuid = UUID.fromString(key);
                var unmuteTime = LocalDateTime.parse(Objects.requireNonNull(tapedPlayersSection.getString(key)));
                ductTape.getTapedPlayers().put(uuid, unmuteTime);
            }
        }
    }
}
