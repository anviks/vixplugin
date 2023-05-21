package me.captainpotatoaim.myplugin.random_commands;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class ZoomCommand implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && sender instanceof final Player player) {
            int cooldownTime = 2200;
            if (cooldowns.containsKey(player.getUniqueId())) {
                long secondsLeft = ((cooldowns.get(player.getUniqueId()) / 1000) + cooldownTime / 1000) - (System.currentTimeMillis() / 1000);
                if (secondsLeft > 0) {
                    // Still cooling down
                    sender.sendMessage("You cant use that command for another " + secondsLeft + " seconds!");
                    return true;
                }
            }

            // No cooldown found or cooldown has expired, save new cooldown
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            GameMode startMode = player.getGameMode();
            player.setVelocity(player.getEyeLocation().getDirection().multiply(100));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);

            BukkitScheduler scheduler = sender.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(Initializer.plugin, () -> player.setGameMode(GameMode.SPECTATOR), 1);
            scheduler.scheduleSyncDelayedTask(Initializer.plugin, () -> player.setGameMode(startMode), 20);
        }
        return true;
    }
}
