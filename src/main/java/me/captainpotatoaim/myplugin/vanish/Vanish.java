package me.captainpotatoaim.myplugin.vanish;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.executors.CommandArguments;
import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class Vanish {

    public void registerCommand() {
        new CommandAPICommand("vanish")
                .withPermission(CommandPermission.OP)
                .executes(this::run)
                .register(Initializer.getPlugin());
    }

    private void removeFromPlayerList(Player player) {
        var packet = new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId()));

        // Send the packet to all online players
        for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
            ((CraftPlayer) onlinePlayer).getHandle().connection.sendPacket(packet);
        }
    }

    private void addToPlayerList(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        var actions = EnumSet.of(
                ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
                ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED
        );

        var packet = new ClientboundPlayerInfoUpdatePacket(actions, List.of(nmsPlayer));

        // Send the packet to all online players
        for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
            ServerPlayer nmsTarget = ((CraftPlayer) onlinePlayer).getHandle();
            nmsTarget.connection.sendPacket(packet);
        }
    }

    private void changePlayerVisibility(Player player, boolean visible) {
        JavaPlugin plugin = Initializer.getPlugin();
        CustomItem.setData(player, "vanished", PersistentDataType.BOOLEAN, !visible);

        if (visible) this.addToPlayerList(player);
        else this.removeFromPlayerList(player);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (visible) onlinePlayer.showPlayer(plugin, player);
            else onlinePlayer.hidePlayer(plugin, player);
        }
    }

    private void run(CommandSender sender, CommandArguments args) {
        if (!(sender instanceof Player player)) return;

        Location location = player.getLocation();
        Optional<Boolean> isVanished = CustomItem.getData(player, "vanished", PersistentDataType.BOOLEAN);

        if (isVanished.isPresent() && isVanished.get()) {
            var lightningLocations = new ArrayList<Location>();

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    Location lightningLocation = location.clone();
                    lightningLocation.setY(lightningLocation.getY() - 1);
                    lightningLocation.setX(lightningLocation.getX() + i);
                    lightningLocation.setZ(lightningLocation.getZ() + j);
                    lightningLocations.add(lightningLocation);
                }
            }

            int delay = 0;
            var scheduler = Bukkit.getScheduler();

            for (int i : List.of(0, 1, 2, 5, 8, 7, 6, 3)) {
                scheduler.runTaskLater(Initializer.getPlugin(), () -> player.getWorld().strikeLightningEffect(lightningLocations.get(i)), delay);
                delay += 5;
            }

            delay += 15;

            scheduler.runTaskLater(Initializer.getPlugin(), () -> {
                for (var lightningLocation : lightningLocations) {
                    player.getWorld().strikeLightningEffect(lightningLocation);
                }
                this.changePlayerVisibility(player, true);
            }, delay);
        } else {
            this.changePlayerVisibility(player, false);
            location.setY(location.getY() + 1);
            player.getWorld().spawnParticle(Particle.LARGE_SMOKE, location, 250, 0.5, 0.5, 0.5, 0.1);
        }
    }
}
