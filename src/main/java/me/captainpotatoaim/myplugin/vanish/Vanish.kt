package me.captainpotatoaim.myplugin.vanish;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.executors.CommandArguments;
import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import me.captainpotatoaim.myplugin.util.CollectionsHelper;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class Vanish implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Optional<Boolean> isVanished = CustomItem.getData(player, "vanished", PersistentDataType.BOOLEAN);

        if (isVanished.isPresent() && isVanished.get()) {
            this.updatePlayerVisibilityForAll(player, false);
        }

        for (Player onlinePlayer : CollectionsHelper.allExcept(Bukkit.getOnlinePlayers(), player)) {
            Optional<Boolean> isSomeoneElseVanished = CustomItem.getData(onlinePlayer, "vanished", PersistentDataType.BOOLEAN);

            if (isSomeoneElseVanished.isPresent() && isSomeoneElseVanished.get()) {
                this.updatePlayerVisibility(onlinePlayer, player, false);
            }
        }
    }

    public void registerCommand() {
        new CommandAPICommand("vanish")
                .withPermission(CommandPermission.OP)
                .executesPlayer(this::run)
                .register(Initializer.getPlugin());
    }

    private ClientboundPlayerInfoRemovePacket getTabListRemovePacket(Player player) {
        return new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId()));
    }

    private ClientboundPlayerInfoUpdatePacket getTabListAddPacket(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        var actions = EnumSet.of(
                ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
                ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED
        );

        return new ClientboundPlayerInfoUpdatePacket(actions, List.of(nmsPlayer));
    }

    private void updatePlayerVisibility(Player target, Player observer, boolean show) {
        var plugin = Initializer.getPlugin();
        var packet = show ? getTabListAddPacket(target) : getTabListRemovePacket(target);

        ServerPlayer nmsObserver = ((CraftPlayer) observer).getHandle();
        nmsObserver.connection.sendPacket(packet);

        if (show) {
            observer.showPlayer(plugin, target);
        } else {
            observer.hidePlayer(plugin, target);
        }
    }

    private void updatePlayerVisibilityForAll(Player player, boolean show) {
        CustomItem.setData(player, "vanished", PersistentDataType.BOOLEAN, !show);

        for (Player onlinePlayer : CollectionsHelper.allExcept(Bukkit.getOnlinePlayers(), player)) {
            updatePlayerVisibility(player, onlinePlayer, show);
        }
    }

    private void run(Player player, CommandArguments args) {
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
                this.updatePlayerVisibilityForAll(player, true);
            }, delay);
        } else {
            this.updatePlayerVisibilityForAll(player, false);
            location.setY(location.getY() + 1);
            player.getWorld().spawnParticle(Particle.LARGE_SMOKE, location, 250, 0.5, 0.5, 0.5, 0.1);
        }
    }
}
