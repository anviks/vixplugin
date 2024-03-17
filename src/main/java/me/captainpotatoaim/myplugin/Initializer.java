package me.captainpotatoaim.myplugin;

import com.mojang.brigadier.CommandDispatcher;
import me.captainpotatoaim.myplugin.custom_items.multi_tool.GiveMultiTool;
import me.captainpotatoaim.myplugin.custom_items.multi_tool.MultiToolListener;
import me.captainpotatoaim.myplugin.custom_items.tnt.GiveCustomFuseTNT;
import me.captainpotatoaim.myplugin.custom_items.tnt.TNTListener;
import me.captainpotatoaim.myplugin.duct_tape.DuctTape;
import me.captainpotatoaim.myplugin.duct_tape.DuctTapeListener;
import me.captainpotatoaim.myplugin.enderman.ArrowListener;
import me.captainpotatoaim.myplugin.enderman.BecomeEnderman;
import me.captainpotatoaim.myplugin.custom_items.explosive_arrows.ExplosiveArrow;
import me.captainpotatoaim.myplugin.custom_items.explosive_arrows.ExplosiveArrowLand;
import me.captainpotatoaim.myplugin.custom_items.explosive_arrows.GiveExplosiveArrow;
import me.captainpotatoaim.myplugin.custom_items.grappling_hook.GiveGrapplingHook;
import me.captainpotatoaim.myplugin.custom_items.grappling_hook.GrapplingHookListener;
import me.captainpotatoaim.myplugin.custom_items.grenade.GiveGrenade;
import me.captainpotatoaim.myplugin.custom_items.grenade.ThrownGrenade;
import me.captainpotatoaim.myplugin.custom_items.railgun.GiveRailgun;
import me.captainpotatoaim.myplugin.custom_items.railgun.TridentListener;
import me.captainpotatoaim.myplugin.random_commands.*;
import me.captainpotatoaim.myplugin.listeners.*;
import me.captainpotatoaim.myplugin.custom_items.rapid_fire_bow.BowListener;
import me.captainpotatoaim.myplugin.custom_items.rapid_fire_bow.GiveBow;
import me.captainpotatoaim.myplugin.random_commands.protect_area.*;
import me.captainpotatoaim.myplugin.sandbox.Inventory;
import me.captainpotatoaim.myplugin.sandbox.SandboxJoinCommand;
import me.captainpotatoaim.myplugin.sandbox.SandboxMainCommand;
import me.captainpotatoaim.myplugin.custom_items.teleport_arrows.GiveTeleportArrow;
import me.captainpotatoaim.myplugin.custom_items.teleport_arrows.TpArrowLand;
import me.captainpotatoaim.myplugin.unfinished_commands.CreeperPrank;
import me.captainpotatoaim.myplugin.unfinished_commands.CreeperPrankWithTp;
import me.captainpotatoaim.myplugin.unfinished_commands.GigaChest;
import me.captainpotatoaim.myplugin.unfinished_commands.UnBreakableCommand;
import me.captainpotatoaim.myplugin.vanish.Vanish;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Initializer extends JavaPlugin {

    public static JavaPlugin plugin;
    public static List<World> defaultWorlds = null;
    public static CommandDispatcher<CommandSender> dispatcher;

    @Override
    @SuppressWarnings({"ConstantConditions"})
    public void onEnable() {
        // Plugin startup logic
        System.out.println("VIXPlugin has started. Enjoy!");

        plugin = getPlugin(Initializer.class);
        defaultWorlds = getServer().getWorlds();
        ProtectedAreas.loadAreas();

        List<ShapedRecipe> customRecipes = new ArrayList<>();
        customRecipes.add(ExplosiveArrow.getRecipe());

        for (ShapedRecipe customRecipe : customRecipes) {
            Bukkit.addRecipe(customRecipe);
        }

        // <editor-fold defaultstate="collapsed" desc="register commands">
        getCommand("fishplz").setExecutor(new SusCommand());
        getCommand("loyalsquad").setExecutor(new DogCommand());
        getCommand("slap").setExecutor(new SlapCommand());
        getCommand("fly").setExecutor(new FlightCommand());
        getCommand("inventory").setExecutor(new InventoryCommand());
        getCommand("echest").setExecutor(new EnderChestCommand());
        getCommand("launch").setExecutor(new LaunchCommand());
        getCommand("zoom").setExecutor(new ZoomCommand());
        getCommand("setunbreakable").setExecutor(new UnBreakableCommand());
        getCommand("freeze").setExecutor(new Freeze());
        getCommand("unfreeze").setExecutor(new UnFreeze());
        getCommand("gigachest").setExecutor(new GigaChest());
        getCommand("givegrenade").setExecutor(new GiveGrenade());
        getCommand("giveexplosivearrow").setExecutor(new GiveExplosiveArrow());
        getCommand("god").setExecutor(new GodMode());
        getCommand("enchantanything").setExecutor(new EnchantAnything());
        getCommand("sandbox").setExecutor(new SandboxMainCommand());
        getCommand("giverailgun").setExecutor(new GiveRailgun());
        getCommand("giveteleportarrow").setExecutor(new GiveTeleportArrow());
        getCommand("creep").setExecutor(new CreeperPrank());
        getCommand("creep2").setExecutor(new CreeperPrankWithTp());
        getCommand("tp-up").setExecutor(new TeleportUp());
        getCommand("duct-tape").setExecutor(new DuctTape());
        getCommand("grappling-hook").setExecutor(new GiveGrapplingHook());
        getCommand("rapid-bow").setExecutor(new GiveBow());
        getCommand("vanish").setExecutor(new Vanish());
        getCommand("ender-toggle").setExecutor(new BecomeEnderman());
        getCommand("protect").setExecutor(new ProtectArea());
        getCommand("unprotect").setExecutor(new UnprotectArea());
        getCommand("give-multi-tool").setExecutor(new GiveMultiTool());
        getCommand("give-custom-fuse-tnt").setExecutor(new GiveCustomFuseTNT());
        getCommand("world").setExecutor(new ChangeWorlds());
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="register events">
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new DeathMessages(), this);
        pluginManager.registerEvents(new JoinMessage(), this);
        pluginManager.registerEvents(new BedMessage(), this);
        pluginManager.registerEvents(new Moving(), this);
        pluginManager.registerEvents(new ThrownGrenade(), this);
        pluginManager.registerEvents(new ExplosiveArrowLand(), this);
        pluginManager.registerEvents(new Inventory(), this);
        pluginManager.registerEvents(new TridentListener(), this);
        pluginManager.registerEvents(new TpArrowLand(), this);
        pluginManager.registerEvents(new DuctTapeListener(), this);
        pluginManager.registerEvents(new GrapplingHookListener(), this);
        pluginManager.registerEvents(new BowListener(), this);
        pluginManager.registerEvents(new ArrowListener(), this);
        pluginManager.registerEvents(new BlockListener(), this);
        pluginManager.registerEvents(new MultiToolListener(), this);
        pluginManager.registerEvents(new TNTListener(), this);
        // </editor-fold>
    }

    @Override
    public void onDisable() {
        Bukkit.broadcastMessage("Disabling...");
        ProtectedAreas.saveAreas();

        for (Player player : getServer().getOnlinePlayers()) {
            if (!defaultWorlds.contains(player.getWorld())) {
                SandboxJoinCommand.sandboxedPlayers.get(player.getUniqueId()).revertPlayerState();
            }
        }

        for (World world : getServer().getWorlds()) {
            if (!defaultWorlds.contains(world)) {
                Bukkit.unloadWorld(world, false);
                try {
                    FileUtils.deleteDirectory(world.getWorldFolder());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
