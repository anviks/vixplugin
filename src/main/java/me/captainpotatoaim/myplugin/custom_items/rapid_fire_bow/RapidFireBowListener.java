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

        Runnable shoot = () -> shootArrowTask(event, player, arrowItem, arrowClass);
        BukkitTask task = Bukkit.getScheduler()
                .runTaskTimer(Initializer.plugin, shoot, 10, 10); // BUG-ACCOMMODATION-11113: changed delay from 0 to 10
        shootingPlayers.put(player.getUniqueId(), task);
    }

    private void shootArrowTask(
            EntityShootBowEvent event,
            Player player,
            ItemStack arrowItem,
            Class<? extends AbstractArrow> arrowClass
    ) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            arrowItem = removeIntangibleProjectileTag(arrowItem);
        }

        if (!player.getInventory().containsAtLeast(arrowItem, 1)) {
            stopShooting(player);
            return;
        }

        Vector arrowDirection = event.getProjectile().getVelocity();
        changeArrowDirection(arrowDirection, player);
        applyRandomOffset(arrowDirection);

        ItemStack finalArrowItem = arrowItem;
        AbstractArrow arrowEntity = player.launchProjectile(arrowClass, arrowDirection, (var arrow) -> {
            if (finalArrowItem.getType() == Material.TIPPED_ARROW) {
                arrow.setItemStack(finalArrowItem);

                var meta = (PotionMeta) finalArrowItem.getItemMeta();
                PotionType basePotion = meta.getBasePotionType();

                var tippedArrowEntity = (Arrow) arrow;
                tippedArrowEntity.setBasePotionType(basePotion);
            }
        });

        double pitch = arrowDirection.length() / 10 + 0.8;
        arrowEntity.getWorld().playSound(arrowEntity.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, (float) pitch);

        CustomItem.copyCustomData(arrowItem, arrowEntity);

        if (player.getGameMode() == GameMode.CREATIVE) {
            arrowEntity.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            return;
        }

        ItemStack bow = event.getBow();
        assert bow != null;
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
