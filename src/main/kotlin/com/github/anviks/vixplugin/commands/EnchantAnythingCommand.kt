package com.github.anviks.vixplugin.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EnchantmentArgument
import dev.jorel.commandapi.arguments.EntitySelectorArgument.ManyPlayers
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class EnchantAnythingCommand(private val plugin: JavaPlugin) : CustomCommand {

    override fun register() {
        CommandAPICommand("enchantanything")
            .withPermission("vixplugin.commands.admin")
            .withArguments(ManyPlayers("players"))
            .withArguments(EnchantmentArgument("enchantment"))
            .withArguments(IntegerArgument("level", 0, 255))
            .executes(this::run)
            .register(plugin)
    }

    private fun run(sender: CommandSender, args: CommandArguments) {
        val players = args.getUnchecked<Collection<Player>>("players")!!
        val enchantment = args.get("enchantment") as Enchantment
        val level = args.get("level") as Int

        for (player in players) {
            val itemInMainHand = player.inventory.itemInMainHand
            val itemMeta = itemInMainHand.itemMeta
            itemMeta.addEnchant(enchantment, level, true)
            itemInMainHand.itemMeta = itemMeta
        }
    }
}
