package me.captainpotatoaim.myplugin.rapid_fire_bow;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class BowListener implements Listener {
    private final HashMap<UUID, Integer> shootingPlayers = new HashMap<>();

    @EventHandler
    void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack heldItem = event.getBow();
        Damageable heldBowMeta = (Damageable) heldItem.getItemMeta();
        ItemStack bow = Bow.getBow();
        Damageable bowMeta = (Damageable) bow.getItemMeta();
        bowMeta.setDamage(heldBowMeta.getDamage());
        heldBowMeta.getEnchants().forEach((enchantment, level) -> bowMeta.addEnchant(enchantment, level, true));
        bow.setItemMeta(bowMeta);

        if (!heldItem.isSimilar(bow)) {
            return;
        }

        shootArrows(player, event.getProjectile().getVelocity(), event);
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        stopShooting(event.getPlayer().getUniqueId());
    }

    @EventHandler
    void onClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        Bukkit.broadcastMessage(action.toString());
        if (!action.equals(Action.PHYSICAL)) {
            Bukkit.broadcastMessage(action.toString());
            stopShooting(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    void onClick2(EntityDamageByEntityEvent event) {
        Bukkit.broadcastMessage(event.getEntity().toString());
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        stopShooting(event.getEntity().getUniqueId());
    }

    @EventHandler
    void onItemChange(PlayerItemHeldEvent event) {
        stopShooting(event.getPlayer().getUniqueId());
    }

    private void stopShooting(UUID uuid) {
        Integer taskId = shootingPlayers.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private void shootArrows(Player player, Vector arrowDirection, EntityShootBowEvent event) {
        ItemStack itemArrow = event.getConsumable();

        if (itemArrow == null) {
            return;
        }

        Material material = itemArrow.getType();
        Class<? extends AbstractArrow> arrowClass;

        switch (material) {
            case ARROW -> arrowClass = Arrow.class;
            case SPECTRAL_ARROW -> arrowClass = SpectralArrow.class;
            case TIPPED_ARROW -> arrowClass = TippedArrow.class;
            default -> {
                return;
            }
        }

        String identifier = itemArrow.getItemMeta().getPersistentDataContainer().get(Tagger.KEY, PersistentDataType.STRING);
        ItemStack singleArrow = itemArrow.clone();
        singleArrow.setAmount(1);
        ItemStack bow = event.getBow();
        assert bow != null;

        Runnable shoot = () -> shootArrowTask(player, arrowDirection, bow, singleArrow, identifier, arrowClass);
        Bukkit.broadcastMessage(bow.getItemMeta().getPersistentDataContainer().getKeys().toString());

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Initializer.plugin, shoot, 0, 1);
        shootingPlayers.put(player.getUniqueId(), task.getTaskId());
    }

    private void shootArrowTask(Player player,
                                Vector arrowDirection,
                                ItemStack bow,
                                ItemStack singleArrow,
                                String identifier,
                                Class<? extends Projectile> arrowClass) {
        if (!player.getInventory().containsAtLeast(singleArrow, 1)) {
            stopShooting(player.getUniqueId());
            return;
        }

        changeArrowDirection(arrowDirection, player);
        Projectile arrow = player.launchProjectile(arrowClass, arrowDirection);
        applyRandomOffset(arrowDirection);
        arrow.setVelocity(arrowDirection);

        if (identifier != null) {
            Tagger.tagEntity(arrow, identifier);
        }

        AbstractArrow abstractArrow = (AbstractArrow) arrow;

        if (player.getGameMode() == GameMode.CREATIVE) {
            abstractArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            return;
        }

        if (bow.getEnchantmentLevel(Enchantment.ARROW_INFINITE) == 0) {
            player.getInventory().removeItem(singleArrow);
            player.updateInventory();
        } else {
            abstractArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        }

        int unbreakingLevel = bow.getEnchantmentLevel(Enchantment.DURABILITY);
        boolean shouldDamage = Math.random() < 1.0 / (unbreakingLevel + 1);
        int damage = shouldDamage ? 1 : 0;

        if (shouldDamage) {
            damageBow(player, bow);
        }

        PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, bow, damage);
        Bukkit.getPluginManager().callEvent(event);
    }

    private static void changeArrowDirection(Vector arrowDirection, Player player) {
        Vector playerDirection = player.getEyeLocation().getDirection();
        double length = arrowDirection.length();

        arrowDirection.setX(playerDirection.getX());
        arrowDirection.setY(playerDirection.getY());
        arrowDirection.setZ(playerDirection.getZ());
        arrowDirection.multiply(length);
    }

    private static void applyRandomOffset(Vector arrowDirection) {
        double randomOffset = 0.1;
        double offsetX = (Math.random() - 0.5) * randomOffset;
        double offsetY = (Math.random() - 0.5) * randomOffset;
        double offsetZ = (Math.random() - 0.5) * randomOffset;
        arrowDirection.setX(arrowDirection.getX() + offsetX);
        arrowDirection.setY(arrowDirection.getY() + offsetY);
        arrowDirection.setZ(arrowDirection.getZ() + offsetZ);
    }

    private void damageBow(Player player, ItemStack bow) {
        Damageable bowMeta = (Damageable) bow.getItemMeta();
        assert bowMeta != null;
        bowMeta.setDamage(bowMeta.getDamage() + 1);
        bow.setItemMeta(bowMeta);

        if (bow.getType().getMaxDurability() - bowMeta.getDamage() <= 0) {
            player.getInventory().removeItem(bow);
            stopShooting(player.getUniqueId());
            float pitch = (float) (Math.random() * 0.4 + 0.8);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, pitch);
            Location eyes = player.getEyeLocation();
            Vector direction = eyes.getDirection();
            player.spawnParticle(Particle.ITEM_CRACK,
                    eyes.add(direction.multiply(0.5)),
                    5,
                    0.1,
                    0.1,
                    0.1,
                    0,
                    bow);
        }
    }
}
