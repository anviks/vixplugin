package com.github.anviks.vixplugin.commands

import com.github.anviks.vixplugin.custom_items.CustomFuseTNT
import com.github.anviks.vixplugin.custom_items.CustomItem
import com.github.anviks.vixplugin.configuration.ConfigDependent
import com.github.anviks.vixplugin.configuration.ConfigOption
import com.github.anviks.vixplugin.util.camelToKebab
import com.github.anviks.vixplugin.util.split
import dev.jorel.commandapi.CommandAPICommand
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
import kotlin.math.roundToInt


@ConfigDependent(ConfigOption.CUSTOM_ITEMS_ENABLED)
class CustomItemCommand(private val plugin: JavaPlugin, customItems: List<CustomItem>) : CustomCommand {

    private val customItems: MutableMap<String, CustomItem> = HashMap()

    init {
        for (item in customItems) {
            val className = item.javaClass.simpleName
            val subCommand = className.camelToKebab()
            this.customItems[subCommand] = item
        }
    }

    override fun register() {
        CommandAPICommand("give-custom")
            .withPermission("vixplugin.commands.givecustom")
            .withArguments(EntitySelectorArgument.ManyPlayers("targets"))
            .split(
                {
                    it.withArguments(MultiLiteralArgument("item", *customItems.keys.minus("custom-fuse-tnt").toTypedArray()))
                        .withOptionalArguments(IntegerArgument("count", 1))
                        .executes(this::giveItem)
                        .register(plugin)
                },
                {
                    it.withArguments(LiteralArgument("custom-fuse-tnt"))
                        .withArguments(FloatArgument("fuse-seconds", 0f))
                        .withOptionalArguments(IntegerArgument("count", 1))
                        .executes(this::giveCustomFuseTnt)
                        .register(plugin)
                }
            )
    }

    private fun giveItem(sender: CommandSender, arguments: CommandArguments) {
        val count = arguments.getByClassOrDefault("count", Int::class.java, 1)
        val item = arguments.getByClass("item", String::class.java)!!
        val itemObj = customItems[item]!!
        val items = itemObj.getItem(count)

        this.giveItemToPlayer(items, arguments)
    }

    private fun giveCustomFuseTnt(sender: CommandSender, arguments: CommandArguments) {
        val fuseTime = arguments.get("fuse-seconds") as Float
        val count = arguments.getOrDefault("count", 1) as Int
        val itemObj = customItems["custom-fuse-tnt"] as CustomFuseTNT
        val items = itemObj.getItem(count, (fuseTime * 20).roundToInt())

        this.giveItemToPlayer(items, arguments)
    }

    private fun giveItemToPlayer(item: ItemStack, arguments: CommandArguments) {
        val targets = arguments.getUnchecked<Collection<Player>>("targets")!!

        for (target in targets) {
            target.inventory.addItem(item)
        }
    }
}