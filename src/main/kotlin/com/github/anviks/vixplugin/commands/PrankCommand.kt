package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.util.face
import com.github.anviks.vixplugin.util.setExperienceDrop
import com.github.anviks.vixplugin.util.setItemDrops
import com.github.anviks.vixplugin.util.withOverloads
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Creeper
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.plugin.Plugin
import kotlin.random.Random


class PrankCommand(private val plugin: Plugin) : CustomCommand {

    override fun getCommands(): List<CommandAPICommand> {
        return CommandAPICommand("prank")
            .withPermission("vixplugin.commands.fun")
            .withArguments(EntitySelectorArgument.ManyPlayers("targets"))
            .withOverloads(
                {
                    it.withArguments(LiteralArgument("creeper"))
                        .executes(::runCreeperPrank)
                },
                {
                    it.withArguments(LiteralArgument("elder-guardian"))
                        .executes(::runElderGuardianPrank)
                },
                {
                    it.withArguments(LiteralArgument("evil"))
                        .executes(::runEvilPrank)
                },
                {
                    it.withArguments(LiteralArgument("arrow"))
                        .executes(::runArrowHitPrank)
                }
            )
    }

    private fun runArrowHitPrank(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!
        targets.forEach {
            val location = it.eyeLocation
            location.pitch = 0f
            val direction = location.direction
            val locationBehind = location.subtract(direction)
            it.world.spawnArrow(locationBehind, direction, 1f, 1f)
        }
    }

    private fun runElderGuardianPrank(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!
        targets.forEach { it.showElderGuardian() }
    }

    private fun runCreeperPrank(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!

        for (target in targets) {
            val location = this.getRandomLocationBehindPlayer(target)
            val creeper = location.world.spawnEntity(location, EntityType.CREEPER) as Creeper
            creeper.face(target.location)
            creeper.setAI(false)
            creeper.health = 1.0
            creeper.setExperienceDrop(0)
            creeper.setItemDrops(listOf())

            target.playSound(location, Sound.ENTITY_CREEPER_PRIMED, 1f, .5f)
        }
    }

    private fun runEvilPrank(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!

        for (target in targets) {
            val amount = Random.nextInt(5, 20)

            repeat(amount) {
                val wolf = target.world.spawnEntity(target.location, EntityType.WOLF) as Wolf
                wolf.customName(text("Doggo"))
                wolf.owner = target
                Bukkit.getScheduler().runTaskLater(plugin, { -> wolf.damage(50.0, target) }, 400)
            }
        }
    }

    private fun getRandomLocationBehindPlayer(target: Player): Location {
        val angle = (-0.5 + Math.random()) * Math.PI
        val location = target.location
        location.pitch = 0f
        val direction = location.direction
        direction.y = 0.0
        direction.rotateAroundY(angle)
        direction.multiply(-1)
        val locationBehind = direction.toLocation(target.world)

        return locationBehind.add(target.location)
    }
}