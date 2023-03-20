package me.captainpotatoaim.myplugin;

import me.captainpotatoaim.myplugin.explosive_arrows.ExplosiveArrowLand;
import me.captainpotatoaim.myplugin.explosive_arrows.GiveExplosiveArrow;
import me.captainpotatoaim.myplugin.grenade.GiveGrenade;
import me.captainpotatoaim.myplugin.grenade.ThrownGrenade;
import me.captainpotatoaim.myplugin.railgun.GiveRailGun;
import me.captainpotatoaim.myplugin.railgun.TridentListener;
import me.captainpotatoaim.myplugin.random_commands.*;
import me.captainpotatoaim.myplugin.listeners.*;
import me.captainpotatoaim.myplugin.sandbox.Inventory;
import me.captainpotatoaim.myplugin.sandbox.SandboxJoinCommand;
import me.captainpotatoaim.myplugin.sandbox.SandboxMainCommand;
import me.captainpotatoaim.myplugin.teleport_arrows.GiveTeleportArrow;
import me.captainpotatoaim.myplugin.teleport_arrows.TpArrowLand;
import me.captainpotatoaim.myplugin.unfinished_commands.CreeperPrank;
import me.captainpotatoaim.myplugin.unfinished_commands.CreeperPrankWithTp;
import me.captainpotatoaim.myplugin.unfinished_commands.GigaChest;
import me.captainpotatoaim.myplugin.unfinished_commands.UnBreakableCommand;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public final class Initializer extends JavaPlugin {

    public static List<World> defaultWorlds = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("VIXPlugin has started. Enjoy!");

        defaultWorlds = getServer().getWorlds();

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
        getCommand("giverailgun").setExecutor(new GiveRailGun());
        getCommand("giveteleportarrow").setExecutor(new GiveTeleportArrow());
        getCommand("creep").setExecutor(new CreeperPrank());
        getCommand("creep2").setExecutor(new CreeperPrankWithTp());
        getCommand("tp-up").setExecutor(new TeleportUp());

        getServer().getPluginManager().registerEvents(new DeathMessages(), this);
        getServer().getPluginManager().registerEvents(new JoinMessage(), this);
        getServer().getPluginManager().registerEvents(new BedMessage(), this);
        getServer().getPluginManager().registerEvents(new Moving(), this);
        getServer().getPluginManager().registerEvents(new ThrownGrenade(), this);
        getServer().getPluginManager().registerEvents(new ExplosiveArrowLand(), this);
        getServer().getPluginManager().registerEvents(new Inventory(), this);
        getServer().getPluginManager().registerEvents(new TridentListener(), this);
        getServer().getPluginManager().registerEvents(new TpArrowLand(), this);
    }

    @Override
    public void onDisable() {

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
