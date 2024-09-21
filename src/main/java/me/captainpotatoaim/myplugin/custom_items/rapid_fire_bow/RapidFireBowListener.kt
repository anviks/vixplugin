package me.captainpotatoaim.myplugin.custom_items.rapid_fire_bow

import me.captainpotatoaim.myplugin.Initializer
import me.captainpotatoaim.myplugin.custom_items.CustomItem
import me.captainpotatoaim.myplugin.util.PDCManager
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.SpectralArrow
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.scheduler.BukkitTask
import java.util.*

class RapidFireBowListener : Listener {

    private val shootingPlayers: MutableMap<UUID, BukkitTask> = HashMap()

    @EventHandler
    fun onBowShoot(event: EntityShootBowEvent) {
        val player = event.entity as? Player ?: return
        if (CustomItem.isOfType(event.bow!!, RapidFireBow::class.java)) {
//            event.setCancelled(true);  // BUG-ACCOMMODATION-11113: commented this line
            shootArrows(player, event)
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        stopShooting(event.player)
    }

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        val action = event.action
        if (action != Action.PHYSICAL) {
            stopShooting(event.player)
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        stopShooting(event.entity)
    }

    @EventHandler
    fun onItemChange(event: PlayerItemHeldEvent) {
        stopShooting(event.player)
    }

    private fun stopShooting(player: Player) {
        val task = shootingPlayers.remove(player.uniqueId)
        task?.cancel()
    }

    private fun shootArrows(player: Player, event: EntityShootBowEvent) {
        val arrowItem = event.consumable ?: return
        val material = arrowItem.type

        val arrowClass = when (material) {
            Material.ARROW, Material.TIPPED_ARROW -> Arrow::class.java
            Material.SPECTRAL_ARROW -> SpectralArrow::class.java
            else -> {
                return
            }
        }

        val bow = checkNotNull(event.bow)
        val quickChargeLevel = bow.getEnchantmentLevel(Enchantment.QUICK_CHARGE)
        val shotDelay = when (quickChargeLevel) {
            0 -> 10L
            1 -> 5L
            2 -> 3L
            else -> 1L
        }

        val task = Bukkit.getScheduler().runTaskTimer(
            Initializer.getPlugin(),
            Runnable { shootArrowTask(player, bow, arrowItem, event.force, arrowClass) },
            shotDelay,  // BUG-ACCOMMODATION-11113: changed delay from 0 to shotDelay
            shotDelay
        )
        shootingPlayers[player.uniqueId] = task
    }

    private fun shootArrowTask(
        player: Player,
        bow: ItemStack,
        arrowItem: ItemStack,
        arrowForce: Float,
        arrowClass: Class<out AbstractArrow?>
    ) {
        var arrowItem = arrowItem
        if (player.gameMode == GameMode.CREATIVE) {
            arrowItem = removeIntangibleProjectileTag(arrowItem)
        }

        if (!player.inventory.containsAtLeast(arrowItem, 1)) {
            stopShooting(player)
            return
        }

        val eyeLocation = player.eyeLocation
        eyeLocation.y -= 0.1
        val arrowEntity = player.world.spawnArrow(
            eyeLocation,
            eyeLocation.direction,
            arrowForce,
            1f,
            arrowClass
        )!!

        arrowEntity.shooter = player
        arrowEntity.itemStack = arrowItem

        if (arrowItem.type == Material.TIPPED_ARROW) {
            val meta = arrowItem.itemMeta as PotionMeta
            val basePotion = meta.basePotionType

            val tippedArrowEntity = arrowEntity as Arrow
            tippedArrowEntity.basePotionType = basePotion
        }

        if (arrowForce.toDouble() == 3.0) {
            arrowEntity.isCritical = true
        }

        val pitch = arrowForce / 10 + 0.8
        arrowEntity.world.playSound(arrowEntity.location, Sound.ENTITY_ARROW_SHOOT, 1f, pitch.toFloat())

        PDCManager.copyCustomData(arrowItem, arrowEntity)

        if (player.gameMode == GameMode.CREATIVE) {
            arrowEntity.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
            return
        }

        if (bow.getEnchantmentLevel(Enchantment.INFINITY) == 0) {
            player.inventory.removeItem(arrowItem)
        } else {
            arrowEntity.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
        }

        val unbreakingLevel = bow.getEnchantmentLevel(Enchantment.UNBREAKING)
        val shouldDamage = Math.random() < 1.0 / (unbreakingLevel + 1)
        val damage = if (shouldDamage) 1 else 0

        if (shouldDamage) {
            damageBow(player, bow)
        }

        val damageEvent = PlayerItemDamageEvent(player, bow, damage, 1)
        Bukkit.getPluginManager().callEvent(damageEvent)
    }

    private fun damageBow(player: Player, bow: ItemStack) {
        val bowMeta = checkNotNull(bow.itemMeta as Damageable)
        bowMeta.damage += 1
        bow.setItemMeta(bowMeta)

        if (bow.type.maxDurability - bowMeta.damage <= 0) {
            player.inventory.removeItem(bow)
            stopShooting(player)
            val pitch = (Math.random() * 0.4 + 0.8).toFloat()
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, pitch)
            val eyes = player.eyeLocation
            val direction = eyes.direction
            player.spawnParticle(
                Particle.ITEM,
                eyes.add(direction.multiply(0.5)),
                5,
                0.1,
                0.1,
                0.1,
                0.0,
                bow
            )
        }
    }

    companion object {

        /**
         * MC 1.21 introduced a new tag for arrows shot in creative mode. That tag gets added at the moment of shooting
         * the arrow, making comparisons with it fail. This method removes the tag from the item.
         */
        private fun removeIntangibleProjectileTag(arrowItem: ItemStack): ItemStack {
            val unwantedTag = "minecraft:intangible_projectile=\\{}"
            val replacementPattern = String.format("(?<=\\[)%s,?|,%s", unwantedTag, unwantedTag)
            val arrowMeta = arrowItem.itemMeta

            // Example - [minecraft:enchantments={levels: {"minecraft:efficiency": 2}},minecraft:intangible_projectile={}]
            var componentString = arrowMeta.asComponentString
            // Example - [minecraft:enchantments={levels: {"minecraft:efficiency": 2}}]
            componentString = componentString.replaceFirst(replacementPattern.toRegex(), "")
            // Example - minecraft:arrow
            val itemTypeKey = arrowItem.type.key.toString()
            // Example - minecraft:arrow[minecraft:enchantments={levels: {"minecraft:efficiency": 2}}]
            val itemAsString = itemTypeKey + componentString

            return Bukkit.getItemFactory().createItemStack(itemAsString)
        }
    }
}