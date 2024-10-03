package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.util.isOfCustomType
import com.github.anviks.vixplugin.util.setCustomType
import com.jeff_media.armorequipevent.ArmorEquipEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Fire
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.util.UUID
import kotlin.math.roundToInt

class LaserGoggles(private val plugin: Plugin) : CustomItem, Listener {

    private val laserTasks = mutableMapOf<UUID, BukkitTask>()

    init {
        Bukkit.getOnlinePlayers().forEach {
            if (it.isWearingGoggles()) it.startShootingLaser()
        }
    }

    override fun getItem(count: Int): ItemStack {
        val goggles = ItemStack.of(Material.IRON_HELMET)
        val meta = goggles.itemMeta
        meta.displayName(text("Laser Goggles", GOLD))
        goggles.itemMeta = meta
        goggles.setCustomType(LaserGoggles::class.java)

        return goggles
    }

    @EventHandler
    fun onArmorEquip(event: ArmorEquipEvent) {
        if (event.newArmorPiece?.isOfCustomType(LaserGoggles::class.java) == true) {
            event.player.startShootingLaser()
        } else if (event.oldArmorPiece?.isOfCustomType(LaserGoggles::class.java) == true) {
            event.player.stopShootingLaser()
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (event.player.isWearingGoggles()) {
            event.player.startShootingLaser()
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (event.player.isWearingGoggles()) {
            event.player.stopShootingLaser()
        }
    }

    private fun Player.isWearingGoggles() = this.equipment.helmet?.isOfCustomType(LaserGoggles::class.java) == true

    private fun Player.startShootingLaser() {
        laserTasks[this.uniqueId] =
            Bukkit.getScheduler().runTaskTimer(plugin, { -> shootLaser(this) }, 0, 1)
    }

    private fun Player.stopShootingLaser() {
        laserTasks[this.uniqueId]?.cancel()
        laserTasks.remove(this.uniqueId)
    }

    private fun shootLaser(player: Player) {
        val rayTrace = player.rayTraceBlocks(100.0)
        val block = rayTrace?.hitBlock
        val face = rayTrace?.hitBlockFace
        val entity = player.rayTraceEntities(100)?.hitEntity

        var distance: Double

        if (entity != null) {
            burnEntity(entity)
            distance = player.location.distance(entity.location)
        } else if (block != null && face != null) {
            burnBlock(block, face)
            distance = player.location.distance(block.location)
            if (block.type == Material.FIRE) distance++
        } else {
            distance = 100.0
        }

        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction.multiply(0.1)

        // Perpendicular to the player's eye direction and Y-axis
        val rightVector = eyeLocation.direction.crossProduct(Vector(0, 1, 0)).normalize()

        val rightEyeLocation = player.eyeLocation.add(rightVector.multiply(0.2))
        val leftEyeLocation = player.eyeLocation.add(rightVector.multiply(-1))

        repeat((distance * 10).roundToInt() + 1) {
            eyeLocation.add(direction)
            leftEyeLocation.add(direction)
            rightEyeLocation.add(direction)
            player.world.spawnParticle(Particle.DUST, leftEyeLocation, 1, Particle.DustOptions(Color.RED, .5f))
            player.world.spawnParticle(Particle.DUST, rightEyeLocation, 1, Particle.DustOptions(Color.RED, .5f))
        }
    }

    private fun burnEntity(entity: Entity) {
        entity.fireTicks = 100
    }

    private fun burnBlock(block: Block, blockFace: BlockFace) {
        val neighbourBlock = block.getRelative(blockFace)
        if (neighbourBlock.type != Material.AIR) return

        if (blockFace == BlockFace.UP) {
            neighbourBlock.type = Material.FIRE
        } else if (block.isBurnable) {
            val fireBlockData = Bukkit.createBlockData(Material.FIRE) {
                (it as Fire).setFace(blockFace.getOppositeFace(), true)
            }
            neighbourBlock.blockData = fireBlockData
        }
    }
}