package me.captainpotatoaim.myplugin.rapid_fire_bow;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.explosive_arrows.ExplosiveArrow;
import me.captainpotatoaim.myplugin.util.Inventory;
import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import javax.swing.*;
import java.util.*;

public class BowListener implements Listener {
    private final HashMap<UUID, Integer> drawingPlayers = new HashMap<>();

    @EventHandler
    public void onBowDraw(PlayerInteractEvent event) {
        Bukkit.broadcastMessage(event.getAction().toString());
        Action action = event.getAction();
        Player player = event.getPlayer();

        if (!(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
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
    public void onBowShoot(EntityShootBowEvent event) {
        stopShooting(event.getEntity().getUniqueId());
        Bukkit.broadcastMessage("shot");
    }

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent event) {
        stopShooting(event.getPlayer().getUniqueId());
        Bukkit.broadcastMessage("changed item");
    }

    private void stopShooting(UUID uuid) {
        Bukkit.getScheduler().cancelTask(drawingPlayers.get(uuid));
        drawingPlayers.remove(uuid);
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
        Bukkit.broadcastMessage(Arrays.toString(secret));
        ItemStack clone = itemArrow.clone();
        clone.setAmount(1);

        Runnable shoot = () -> {
            Projectile arrow = player.launchProjectile(arrowClass, player.getEyeLocation().getDirection());
            player.getInventory().removeItem(clone);
            player.updateInventory();
            if (secret != null) {
                Tagger.tagEntity(arrow, secret);
            }
        };

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(Initializer.class), shoot, 0, 2);
        Bukkit.broadcastMessage("Task is running");
        Bukkit.broadcastMessage(String.valueOf(task.isCancelled()));
        drawingPlayers.put(player.getUniqueId(), task.getTaskId());
    }
}
