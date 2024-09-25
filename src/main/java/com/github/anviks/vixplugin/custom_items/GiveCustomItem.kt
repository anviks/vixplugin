package com.github.anviks.vixplugin.custom_items

import com.github.anviks.vixplugin.CustomCommand
import com.github.anviks.vixplugin.util.camelToKebab
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.arguments.FloatArgument
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.MultiLiteralArgument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class GiveCustomItem(private val plugin: JavaPlugin, customItems: Array<CustomItem>) : CustomCommand {

    private val customItems: MutableMap<String, CustomItem> = HashMap()

    init {
        for (item in customItems) {
            val className = item.javaClass.simpleName
            val subCommand = className.camelToKebab()
            this.customItems[subCommand] = item
        }
    }

    override fun register() {
        val baseGiveCommand = CommandAPICommand("give-custom")
            .withPermission(CommandPermission.OP)
            .withArguments(EntitySelectorArgument.ManyPlayers("targets"))

        baseGiveCommand.copy()
            .withArguments(MultiLiteralArgument("item", *customItems.keys.minus("custom-fuse-tnt").toTypedArray()))
            .withOptionalArguments(IntegerArgument("count", 1))
            .executes(this::giveItem)
            .register(plugin)

        baseGiveCommand
            .withArguments(LiteralArgument("custom-fuse-tnt"))
            .withArguments(FloatArgument("fuse-seconds", 0f))
            .withOptionalArguments(IntegerArgument("count", 1))
            .executes(this::giveCustomFuseTnt)
            .register(plugin)
    }

    private fun giveItem(sender: CommandSender, arguments: CommandArguments) {
        val count = arguments.getByClassOrDefault("count", Int::class.java, 1)
        val item = arguments.getByClass("item", String::class.java)!!
        val itemObj = customItems[item]!!
        val items = itemObj.getItem(count)

        this.giveItemToPlayer(items, arguments)
    }

    private fun giveCustomFuseTnt(sender: CommandSender, arguments: CommandArguments) {
        val count = arguments.getByClassOrDefault("count", Int::class.java, 1)
        val fuseTime = arguments.getByClass("fuse-seconds", Float::class.java)!!
        val itemObj = customItems["custom-fuse-tnt"] as CustomFuseTNT
        val items = itemObj.getItem(count, fuseTime)

        this.giveItemToPlayer(items, arguments)
    }

    private fun giveItemToPlayer(item: ItemStack, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!

        for (target in targets) {
            target.inventory.addItem(item)
        }
    }
}