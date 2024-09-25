package com.github.anviks.vixplugin.random_commands

import com.github.anviks.vixplugin.CustomCommand
import com.github.anviks.vixplugin.util.face
import com.github.anviks.vixplugin.util.setExperienceDrop
import com.github.anviks.vixplugin.util.setItemDrops
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Creeper
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector


class PrankCommand(private val plugin: JavaPlugin) : CustomCommand {

    override fun register() {
        val baseCommand = CommandAPICommand("prank")
            .withPermission(CommandPermission.OP)
            .withArguments(EntitySelectorArgument.ManyPlayers("targets"))

        baseCommand.copy()
            .withArguments(LiteralArgument("creeper"))
            .executes(this::runCreeperPrank)
            .register(this.plugin)

        baseCommand
            .withArguments(LiteralArgument("elder-guardian"))
            .executes(this::runElderGuardianPrank)
            .register(this.plugin)
    }

    private fun runElderGuardianPrank(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!

        for (target in targets) {
            target.showElderGuardian()
        }
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