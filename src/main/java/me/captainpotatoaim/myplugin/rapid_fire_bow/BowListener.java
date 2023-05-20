package me.captainpotatoaim.myplugin.rapid_fire_bow;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.util.Inventory;
import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class BowListener implements Listener {
    private final HashMap<UUID, Integer> shootingPlayers = new HashMap<>();

    @EventHandler
    void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (!heldItem.getType().equals(Material.BOW)) {
            return;
        }

        Damageable heldBowMeta = (Damageable) heldItem.getItemMeta();
        ItemStack bow = Bow.getBow();
        Damageable bowMeta = (Damageable) bow.getItemMeta();
        bowMeta.setDamage(heldBowMeta.getDamage());
        bow.setItemMeta(bowMeta);

        if (!heldItem.isSimilar(bow)) {
            return;
        }

        shootArrows(player);
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        stopShooting(event.getPlayer().getUniqueId());
    }

    @EventHandler
    void onClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.equals(Action.PHYSICAL)) {
            stopShooting(event.getPlayer().getUniqueId());
            Bukkit.broadcastMessage("shot");
        }
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        stopShooting(event.getEntity().getUniqueId());
    }

    @EventHandler
    void onItemChange(PlayerItemHeldEvent event) {
        stopShooting(event.getPlayer().getUniqueId());
        Bukkit.broadcastMessage("changed item");
    }

    private void stopShooting(UUID uuid) {
        Integer taskId = shootingPlayers.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(shootingPlayers.get(uuid));
        }
    }

    private void shootArrows(Player player) {
        ItemStack itemArrow = Inventory.getShootableArrow(player).orElse(null);

        if (itemArrow == null) {
            return;
        }

        Material material = itemArrow.getType();
        Class<? extends AbstractArrow> arrowClass;

        switch (material) {
            case ARROW -> arrowClass = Arrow.class;
            case SPECTRAL_ARROW -> arrowClass = SpectralArrow.class;
            case TIPPED_ARROW -> arrowClass = TippedArrow.class;
            default -> arrowClass = null;
        }

        if (arrowClass == null) {
            return;
        }

        byte[] secret = itemArrow.getItemMeta().getPersistentDataContainer().get(Tagger.key, PersistentDataType.BYTE_ARRAY);
        ItemStack clone = itemArrow.clone();
        clone.setAmount(1);

        Runnable shoot = () -> {
            Projectile arrow = player.launchProjectile(arrowClass, player.getEyeLocation().getDirection());
            if (!player.getInventory().removeItem(clone).isEmpty()) {
                stopShooting(player.getUniqueId());
            }
            player.updateInventory();
            if (secret != null) {
                Tagger.tagEntity(arrow, secret);
            }
        };

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(Initializer.class), shoot, 0, 2);
        shootingPlayers.put(player.getUniqueId(), task.getTaskId());
    }
}
