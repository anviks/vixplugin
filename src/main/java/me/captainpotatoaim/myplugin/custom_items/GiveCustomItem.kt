package me.captainpotatoaim.myplugin.custom_items

import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.EntitySelectorArgument.ManyPlayers
import dev.jorel.commandapi.arguments.FloatArgument
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import me.captainpotatoaim.myplugin.CustomCommand
import me.captainpotatoaim.myplugin.custom_items.custom_fuse_tnt.CustomFuseTNT.Companion.getItem
import me.captainpotatoaim.myplugin.util.StringHelper.camelCaseToKebabCase
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class GiveCustomItem(private val plugin: JavaPlugin, customItems: Array<CustomItem>) : CustomCommand {

    private val customItems: MutableMap<String?, CustomItem> = HashMap()

    init {
        for (item in customItems) {
            val className = item.javaClass.simpleName
            val subCommand = camelCaseToKebabCase(className)
            this.customItems[subCommand] = item
        }
    }

    override fun register() {
        val targetsParam = "targets"
        val countParam = "count"
        val itemParam = "item"
        val fuseSecondsParam = "fuse-seconds"
        val customFuseTntParam = "custom-fuse-tnt"

        val baseGiveCommand = CommandAPICommand("give-custom")
            .withPermission(CommandPermission.OP)
            .withArguments(ManyPlayers(targetsParam))

        baseGiveCommand.copy()
            .withArguments(StringArgument(itemParam).replaceSuggestions { sender: SuggestionInfo<CommandSender?>?, context: SuggestionsBuilder ->
                customItems.keys.forEach(
                    Consumer { text: String? -> context.suggest(text) })
                CompletableFuture.completedFuture(context.build())
            })
            .withOptionalArguments(IntegerArgument(countParam, 1))
            .executes(CommandExecutor { _: CommandSender?, commandArguments: CommandArguments ->
                val targets = commandArguments.getUnchecked<Collection<Player>>(targetsParam)!!
                val item = commandArguments.getByClass(itemParam, String::class.java)
                val count = commandArguments.getByClassOrDefault(countParam, Int::class.java, 1)
                val itemObj = customItems[item]

                for (target in targets) {
                    target.inventory.addItem(itemObj!!.getItem(count))
                }
            })
            .register(plugin)

        baseGiveCommand
            .withArguments(LiteralArgument(customFuseTntParam))
            .withArguments(FloatArgument(fuseSecondsParam, 0f))
            .withOptionalArguments(IntegerArgument(countParam, 1))
            .executes(CommandExecutor { _: CommandSender?, commandArguments: CommandArguments ->
                val targets = commandArguments.getUnchecked<Collection<Player>>(targetsParam)!!
                val count = commandArguments.getByClassOrDefault(countParam, Int::class.java, 1)
                val fuseTime = commandArguments.getByClass(fuseSecondsParam, Float::class.java)!!

                for (target in targets) {
                    target.inventory.addItem(getItem(count, fuseTime))
                }
            })
            .register(plugin)
    }
}