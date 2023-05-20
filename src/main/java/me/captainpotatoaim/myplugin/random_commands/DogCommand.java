package me.captainpotatoaim.myplugin.random_commands;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DogCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && sender.isOp()||
                sender instanceof ConsoleCommandSender && args.length > 0 ||
                sender instanceof BlockCommandSender && args.length > 0) {

            Server server = sender.getServer();

            Player target;
            if (args.length > 0) {
                target = Bukkit.getServer().getPlayerExact(args[0]);
            } else {
                target = (Player) sender;
            }

            if (target != null) {
                int number = (int) (Math.random() * 20 + 1);
                for (int i = 0; i < number; i++) {
                    Entity wolf = target.getWorld().spawnEntity(target.getLocation(), EntityType.WOLF);
                    Wolf dog = (Wolf) wolf;
                    dog.setCustomName("Doggo");
                    dog.setOwner(target);
                    server.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Initializer.class), new Runnable() {
                        @Override
                        public void run() {
                            dog.damage(50, target);
                        }
                    }, 400);

                }
            } else {
                sender.sendMessage("That player is not online or doesn't exist.");
            }

        } else {
            sender.sendMessage("That didn't work.");
        }
        return true;
    }
}
