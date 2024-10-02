package com.github.anviks.vixplugin.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

class LaunchCommand(private val plugin: JavaPlugin) : CustomCommand {

    override fun register() {
        CommandAPICommand("launch")
            .withPermission("vixplugin.commands.fun")
            .withArguments(EntitySelectorArgument.ManyEntities("targets"))
            .executes(this::run)
            .register(plugin)
    }

    private fun run(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Entity>>("targets")!!

        for (target in targets) {
            if (target is Player) {
                target.velocity = Vector(0, 100, 0)
            } else {
                target.velocity = Vector(0, 4, 0)
            }

            target.sendMessage(
                if (target.name == sender.name) "I believe I can fly." else "${sender.name} believes you can fly."
            )

            if (target is Player) {
                target.playSound(target.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1f)
            }
        }
    }
}
