package me.captainpotatoaim.myplugin.custom_items

import me.captainpotatoaim.myplugin.Initializer
import me.captainpotatoaim.myplugin.util.copyPDCTo
import me.captainpotatoaim.myplugin.util.copyWithoutIntangibleTag
import me.captainpotatoaim.myplugin.util.toInt
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration.*
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
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
import java.util.UUID

class RapidFireBow : CustomItem(), Listener {

    private val shootingPlayers: MutableMap<UUID, BukkitTask> = HashMap()

    override fun getItem(count: Int): ItemStack {
        val bow = ItemStack(Material.BOW)
        val meta = bow.itemMeta

        val bowName = text("\uD83C\uDFF9 Archer's Minigun 🏹")
            .color(GREEN)
            .decoration(BOLD, true)
            .decoration(ITALIC, false)

        val lore = listOf(
            text("Shoots arrows rapidly.").color(GRAY).decoration(ITALIC, false),
            text("Requires arrows to shoot.").color(GRAY).decoration(ITALIC, false),
            empty(),
            text("TIP: With Quick Charge, the fire rate can").color(DARK_AQUA).decoration(ITALIC, false),
            text("increase to as much as 20 times per second!").color(DARK_AQUA).decoration(ITALIC, false)
        )

        meta.displayName(bowName)
        meta.lore(lore)
        bow.itemMeta = meta

        setType(bow, RapidFireBow::class.java)

        return bow
    }

    @EventHandler
    fun onBowShoot(event: EntityShootBowEvent) {
        val player = event.entity as? Player ?: return
        if (isOfType(event.bow!!, RapidFireBow::class.java)) {
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
        val bow = event.bow ?: return
        val arrowItem = event.consumable ?: return
        val arrowClass = getArrowClass(arrowItem.type) ?: return
        val shotDelay = this.calculateShotDelay(bow)

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
            arrowItem = arrowItem.copyWithoutIntangibleTag()
        }

        if (!player.inventory.containsAtLeast(arrowItem, 1)) {
            stopShooting(player)
            return
        }

        val arrowEntity = this.spawnArrowEntity(player, arrowForce, arrowClass)
        this.configureArrowEntity(player, arrowItem, arrowEntity, arrowForce)

        if (player.gameMode == GameMode.CREATIVE) {
            arrowEntity.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
            return
        }

        if (bow.getEnchantmentLevel(Enchantment.INFINITY) == 0) {
            player.inventory.removeItem(arrowItem)
        } else {
            arrowEntity.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
        }

        val damage = this.calculateDamage(bow)
        if (damage > 0) this.damageBow(player, bow)

        val damageEvent = PlayerItemDamageEvent(player, bow, damage, 1)
        Bukkit.getPluginManager().callEvent(damageEvent)
    }

    private fun spawnArrowEntity(
        player: Player,
        arrowForce: Float,
        arrowClass: Class<out AbstractArrow?>
    ): AbstractArrow {
        val eyeLocation = player.eyeLocation.apply { y -= 0.1 }
        return player.world.spawnArrow(
            eyeLocation,
            eyeLocation.direction,
            arrowForce,
            1f,
            arrowClass
        )
    }

    private fun configureArrowEntity(
        player: Player,
        arrowItem: ItemStack,
        arrowEntity: AbstractArrow,
        arrowForce: Float
    ) {
        arrowEntity.shooter = player
        arrowEntity.itemStack = arrowItem

        if (arrowItem.type == Material.TIPPED_ARROW) {
            val meta = arrowItem.itemMeta as PotionMeta
            (arrowEntity as Arrow).basePotionType = meta.basePotionType
        }

        if (arrowForce.toDouble() == 3.0) {
            arrowEntity.isCritical = true
        }

        val pitch = arrowForce / 10 + 0.8
        arrowEntity.world.playSound(arrowEntity.location, Sound.ENTITY_ARROW_SHOOT, 1f, pitch.toFloat())
        arrowItem.copyPDCTo(arrowEntity)
    }

    private fun getArrowClass(material: Material): Class<out AbstractArrow>? {
        return when (material) {
            Material.ARROW, Material.TIPPED_ARROW -> Arrow::class.java
            Material.SPECTRAL_ARROW -> SpectralArrow::class.java
            else -> null
        }
    }

    private fun calculateShotDelay(bow: ItemStack): Long {
        val quickChargeLevel = bow.getEnchantmentLevel(Enchantment.QUICK_CHARGE)
        return when (quickChargeLevel) {
            0 -> 10L
            1 -> 5L
            2 -> 3L
            else -> 1L
        }
    }

    private fun calculateDamage(bow: ItemStack): Int {
        val unbreakingLevel = bow.getEnchantmentLevel(Enchantment.UNBREAKING)
        val shouldDamage = Math.random() < 1.0 / (unbreakingLevel + 1)
        return shouldDamage.toInt()
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
}