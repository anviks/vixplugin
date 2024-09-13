package me.captainpotatoaim.myplugin.custom_items.rapid_fire_bow;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import me.captainpotatoaim.myplugin.util.Tagger;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class RapidFireBowListener implements Listener {
    private final Map<UUID, BukkitTask> shootingPlayers = new HashMap<>();

    @EventHandler
    void onBowShoot(EntityShootBowEvent event) {
        if (CustomItem.isOfType(event.getBow(), RapidFireBow.class)
                && event.getEntity() instanceof Player player) {
            event.setCancelled(true);
            shootArrows(player, event);
        }
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        stopShooting(event.getPlayer());
    }

    @EventHandler
    void onClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.equals(Action.PHYSICAL)) {
            stopShooting(event.getPlayer());
        }
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        stopShooting(event.getEntity());
    }

    @EventHandler
    void onItemChange(PlayerItemHeldEvent event) {
        stopShooting(event.getPlayer());
    }

    private void stopShooting(Player player) {
        var task = shootingPlayers.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }

    private void shootArrows(Player player, EntityShootBowEvent event) {
        ItemStack itemArrow = event.getConsumable();
        if (itemArrow == null) return;

        Material material = itemArrow.getType();
        Class<? extends AbstractArrow> arrowClass;

        switch (material) {
            case ARROW, TIPPED_ARROW -> arrowClass = Arrow.class;
            case SPECTRAL_ARROW -> arrowClass = SpectralArrow.class;
            default -> {
                return;
            }
        }

        PersistentDataContainer persistentDataContainer = itemArrow.getItemMeta().getPersistentDataContainer();
        String identifier = persistentDataContainer.get(Tagger.CUSTOM_ITEM_KEY, PersistentDataType.STRING);
        ItemStack bow = event.getBow();
        assert bow != null;

        Vector arrowDirection = event.getProjectile().getVelocity();
        Runnable shoot = () -> shootArrowTask(player, arrowDirection, bow, itemArrow, identifier, arrowClass);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Initializer.plugin, shoot, 0, 10);
        shootingPlayers.put(player.getUniqueId(), task);
    }

    private void shootArrowTask(
            Player player,
            Vector arrowDirection,
            ItemStack bow,
            ItemStack arrowItem,
            String identifier,
            Class<? extends AbstractArrow> arrowClass
    ) {
        if (!player.getInventory().containsAtLeast(arrowItem, 1)) {
            stopShooting(player);
            return;
        }

        changeArrowDirection(arrowDirection, player);
        applyRandomOffset(arrowDirection);
        AbstractArrow arrowEntity = player.launchProjectile(arrowClass, arrowDirection, (var arrow) -> {
            if (arrowItem.getType() == Material.TIPPED_ARROW) {
                var meta = (PotionMeta) arrowItem.getItemMeta();
                PotionType basePotion = meta.getBasePotionType();
                List<PotionEffect> potionEffects = meta.getCustomEffects();

                var tippedArrowEntity = (Arrow) arrow;
                tippedArrowEntity.setBasePotionType(basePotion);
                for (PotionEffect effect : potionEffects) {
                    tippedArrowEntity.addCustomEffect(effect, true);
                }
            }
        });

        double pitch = arrowDirection.length() / 10 + 0.8;
        arrowEntity.getWorld().playSound(arrowEntity.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, (float) pitch);

        if (identifier != null) {
            Tagger.addIdentifier(arrowEntity, identifier);
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            arrowEntity.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            return;
        }

        if (bow.getEnchantmentLevel(Enchantment.ARROW_INFINITE) == 0) {
            arrowItem.setAmount(arrowItem.getAmount() - 1);
        } else {
            arrowEntity.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        }

        int unbreakingLevel = bow.getEnchantmentLevel(Enchantment.DURABILITY);
        boolean shouldDamage = Math.random() < 1.0 / (unbreakingLevel + 1);
        int damage = shouldDamage ? 1 : 0;

        if (shouldDamage) {
            damageBow(player, bow);
        }

        PlayerItemDamageEvent damageEvent = new PlayerItemDamageEvent(player, bow, damage, 1);
        Bukkit.getPluginManager().callEvent(damageEvent);
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
            stopShooting(player);
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
