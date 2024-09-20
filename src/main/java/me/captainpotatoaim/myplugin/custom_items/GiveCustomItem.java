package me.captainpotatoaim.myplugin.custom_items;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import me.captainpotatoaim.myplugin.CustomCommand;
import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.custom_items.custom_fuse_tnt.CustomFuseTNT;
import me.captainpotatoaim.myplugin.util.StringHelper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GiveCustomItem implements CustomCommand {

    private final Map<String, CustomItem> customItems = new HashMap<>();

    public GiveCustomItem(CustomItem[] customItems) {
        for (CustomItem item : customItems) {
            String className = item.getClass().getSimpleName();
            String subCommand = StringHelper.camelCaseToKebabCase(className);
            this.customItems.put(subCommand, item);
        }
    }

    @Override
    public void register(JavaPlugin plugin) {
        String targetsParam = "targets";
        String countParam = "count";
        String itemParam = "item";
        String fuseSecondsParam = "fuse-seconds";
        String customFuseTntParam = "custom-fuse-tnt";

        CommandAPICommand baseGiveCommand = new CommandAPICommand("give-custom")
                .withPermission(CommandPermission.OP)
                .withArguments(new EntitySelectorArgument.ManyPlayers(targetsParam));

        baseGiveCommand.copy()
                .withArguments(new StringArgument(itemParam).replaceSuggestions((sender, context) -> {
                    customItems.keySet().forEach(context::suggest);
                    return CompletableFuture.completedFuture(context.build());
                }))
                .withOptionalArguments(new IntegerArgument(countParam, 1))
                .executes((commandSender, commandArguments) -> {
                    var targets = commandArguments.<Collection<Player>>getUnchecked(targetsParam);
                    var item = commandArguments.getByClass(itemParam, String.class);
                    var count = commandArguments.getByClassOrDefault(countParam, Integer.class, 1);
                    var itemObj = this.customItems.get(item);

                    assert targets != null;

                    for (Player target : targets) {
                        target.getInventory().addItem(itemObj.getItem(count));
                    }
                })
                .register(plugin);

        baseGiveCommand
                .withArguments(new LiteralArgument(customFuseTntParam))
                .withArguments(new FloatArgument(fuseSecondsParam, 0))
                .withOptionalArguments(new IntegerArgument(countParam, 1))
                .executes((commandSender, commandArguments) -> {
                    var targets = commandArguments.<Collection<Player>>getUnchecked(targetsParam);
                    var count = commandArguments.getByClassOrDefault(countParam, Integer.class, 1);
                    var fuseTime = commandArguments.getByClass(fuseSecondsParam, Float.class);

                    assert targets != null;
                    assert fuseTime != null;

                    for (Player target : targets) {
                        target.getInventory().addItem(CustomFuseTNT.getItem(count, fuseTime));
                    }
                })
                .register(plugin);
    }
}
