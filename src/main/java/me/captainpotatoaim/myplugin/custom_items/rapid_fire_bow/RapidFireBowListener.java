package me.captainpotatoaim.myplugin.custom_items.rapid_fire_bow;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RapidFireBowListener implements Listener {
    private final Map<UUID, BukkitTask> shootingPlayers = new HashMap<>();

    @EventHandler
    void onBowShoot(EntityShootBowEvent event) {
        if (CustomItem.isOfType(event.getBow(), RapidFireBow.class)
                && event.getEntity() instanceof Player player) {
//            event.setCancelled(true);  // BUG-ACCOMMODATION-11113: commented this line
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
        ItemStack arrowItem = event.getConsumable();
        if (arrowItem == null) return;

        Material material = arrowItem.getType();
        Class<? extends AbstractArrow> arrowClass;

        switch (material) {
            case ARROW, TIPPED_ARROW -> arrowClass = Arrow.class;
            case SPECTRAL_ARROW -> arrowClass = SpectralArrow.class;
            default -> {
                return;
            }
        }

        ItemStack bow = event.getBow();
        assert bow != null;
        int quickChargeLevel = bow.getEnchantmentLevel(Enchantment.QUICK_CHARGE);
        int shotDelay = switch (quickChargeLevel) {
            case 0 -> 10;
            case 1 -> 5;
            case 2 -> 3;
            default -> 1;
        };

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                Initializer.getPlugin(),
                () -> shootArrowTask(player, bow, arrowItem, event.getForce(), arrowClass),
                shotDelay,  // BUG-ACCOMMODATION-11113: changed delay from 0 to shotDelay
                shotDelay);
        shootingPlayers.put(player.getUniqueId(), task);
    }

    private void shootArrowTask(
            Player player,
            ItemStack bow,
            ItemStack arrowItem,
            float arrowForce,
            Class<? extends AbstractArrow> arrowClass
    ) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            arrowItem = removeIntangibleProjectileTag(arrowItem);
        }

        if (!player.getInventory().containsAtLeast(arrowItem, 1)) {
            stopShooting(player);
            return;
        }

        Location eyeLocation = player.getEyeLocation();
        eyeLocation.setY(eyeLocation.getY() - 0.1);
        AbstractArrow arrowEntity = player.getWorld().spawnArrow(
                eyeLocation,
                eyeLocation.getDirection(),
                arrowForce,
                1,
                arrowClass
        );

        arrowEntity.setShooter(player);
        arrowEntity.setItemStack(arrowItem);

        if (arrowItem.getType() == Material.TIPPED_ARROW) {
            var meta = (PotionMeta) arrowItem.getItemMeta();
            PotionType basePotion = meta.getBasePotionType();

            var tippedArrowEntity = (Arrow) arrowEntity;
            tippedArrowEntity.setBasePotionType(basePotion);
        }

        if (arrowForce == 3.0) {
            arrowEntity.setCritical(true);
        }

        double pitch = arrowForce / 10 + 0.8;
        arrowEntity.getWorld().playSound(arrowEntity.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, (float) pitch);

        CustomItem.copyCustomData(arrowItem, arrowEntity);

        if (player.getGameMode() == GameMode.CREATIVE) {
            arrowEntity.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            return;
        }

        if (bow.getEnchantmentLevel(Enchantment.INFINITY) == 0) {
            player.getInventory().removeItem(arrowItem);
        } else {
            arrowEntity.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        }

        int unbreakingLevel = bow.getEnchantmentLevel(Enchantment.UNBREAKING);
        boolean shouldDamage = Math.random() < 1.0 / (unbreakingLevel + 1);
        int damage = shouldDamage ? 1 : 0;

        if (shouldDamage) {
            damageBow(player, bow);
        }

        PlayerItemDamageEvent damageEvent = new PlayerItemDamageEvent(player, bow, damage, 1);
        Bukkit.getPluginManager().callEvent(damageEvent);
    }

    /**
     * MC 1.21 introduced a new tag for arrows shot in creative mode. That tag gets added at the moment of shooting
     * the arrow, making comparisons with it fail. This method removes the tag from the item.
     */
    private static @NotNull ItemStack removeIntangibleProjectileTag(@NotNull ItemStack arrowItem) {
        String unwantedTag = "minecraft:intangible_projectile=\\{}";
        String replacementPattern = String.format("(?<=\\[)%s,?|,%s", unwantedTag, unwantedTag);
        ItemMeta arrowMeta = arrowItem.getItemMeta();

        // Example - [minecraft:enchantments={levels: {"minecraft:efficiency": 2}},minecraft:intangible_projectile={}]
        String componentString = arrowMeta.getAsComponentString();
        // Example - [minecraft:enchantments={levels: {"minecraft:efficiency": 2}}]
        componentString = componentString.replaceFirst(replacementPattern, "");
        // Example - minecraft:arrow
        String itemTypeKey = arrowItem.getType().getKey().toString();
        // Example - minecraft:arrow[minecraft:enchantments={levels: {"minecraft:efficiency": 2}}]
        String itemAsString = itemTypeKey + componentString;

        return Bukkit.getItemFactory().createItemStack(itemAsString);
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
            player.spawnParticle(Particle.ITEM,
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
