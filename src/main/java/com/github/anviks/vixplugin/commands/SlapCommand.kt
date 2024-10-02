package com.github.anviks.vixplugin.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Damageable
import org.bukkit.entity.Entity
import org.bukkit.plugin.java.JavaPlugin

class SlapCommand(private val plugin: JavaPlugin) : CustomCommand {

    override fun register() {
        CommandAPICommand("slap")
            .withPermission("vixplugin.commands.fun")
            .withArguments(EntitySelectorArgument.ManyEntities("targets"))
            .executes(this::run)
            .register(plugin)
    }

    private fun run(sender: CommandSender, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Entity>>("targets")!!

        for (target in targets) {
            if (target is Damageable) target.damage(2.0)
            target.velocity = target.facing.direction.multiply(-3)
            target.sendMessage(text("You just got slapped by ${sender.name}!", YELLOW))
        }

        sender.sendMessage(text("You just slapped ${targets.size} entities!", GREEN))
    }
}
