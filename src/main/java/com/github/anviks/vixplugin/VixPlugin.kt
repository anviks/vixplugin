package com.github.anviks.vixplugin

import com.github.anviks.vixplugin.custom_items.CustomFuseTNT
import com.github.anviks.vixplugin.custom_items.CustomItem
import com.github.anviks.vixplugin.custom_items.ExplosiveArrow
import com.github.anviks.vixplugin.custom_items.GiveCustomItem
import com.github.anviks.vixplugin.custom_items.GrapplingHook
import com.github.anviks.vixplugin.custom_items.Grenade
import com.github.anviks.vixplugin.custom_items.MultiTool
import com.github.anviks.vixplugin.custom_items.Railgun
import com.github.anviks.vixplugin.custom_items.RapidFireBow
import com.github.anviks.vixplugin.custom_items.TeleportArrow
import com.github.anviks.vixplugin.duct_tape.DuctTape
import com.github.anviks.vixplugin.enderman.ArrowListener
import com.github.anviks.vixplugin.enderman.BecomeEnderman
import com.github.anviks.vixplugin.listeners.BedMessage
import com.github.anviks.vixplugin.listeners.DeathMessages
import com.github.anviks.vixplugin.listeners.EntityListener
import com.github.anviks.vixplugin.listeners.JoinMessage
import com.github.anviks.vixplugin.listeners.Moving
import com.github.anviks.vixplugin.random_commands.ChangeWorlds
import com.github.anviks.vixplugin.random_commands.DogCommand
import com.github.anviks.vixplugin.random_commands.EnchantAnything
import com.github.anviks.vixplugin.random_commands.EnderChestCommand
import com.github.anviks.vixplugin.random_commands.FlightCommand
import com.github.anviks.vixplugin.random_commands.Freeze
import com.github.anviks.vixplugin.random_commands.GodMode
import com.github.anviks.vixplugin.random_commands.InventoryCommand
import com.github.anviks.vixplugin.random_commands.LaunchCommand
import com.github.anviks.vixplugin.random_commands.PrankCommand
import com.github.anviks.vixplugin.random_commands.SlapCommand
import com.github.anviks.vixplugin.random_commands.TeleportUp
import com.github.anviks.vixplugin.random_commands.UnFreeze
import com.github.anviks.vixplugin.random_commands.ZoomCommand
import com.github.anviks.vixplugin.random_commands.protect_area.BlockListener
import com.github.anviks.vixplugin.random_commands.protect_area.ProtectArea
import com.github.anviks.vixplugin.random_commands.protect_area.ProtectedAreas
import com.github.anviks.vixplugin.random_commands.protect_area.UnprotectArea
import com.github.anviks.vixplugin.sandbox.Inventory
import com.github.anviks.vixplugin.vanish.Vanish
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.Listener
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.Objects
import java.util.UUID


class VixPlugin : JavaPlugin() {

    private lateinit var ductTape: DuctTape

    override fun onEnable() {
        plugin = getPlugin(VixPlugin::class.java)
        defaultWorlds = server.worlds
        ProtectedAreas.loadAreas()

        registerCommands()
        registerEvents()
        registerRecipes()

        loadTapedPlayers()
    }

    private fun registerCommands() {
        val commands = mapOf(
            "loyalsquad" to DogCommand(),
            "slap" to SlapCommand(),
            "fly" to FlightCommand(),
            "inventory" to InventoryCommand(),
            "echest" to EnderChestCommand(),
            "launch" to LaunchCommand(),
            "zoom" to ZoomCommand(),
            "freeze" to Freeze(),
            "unfreeze" to UnFreeze(),
            "god" to GodMode(),
            //  "sandbox" to SandboxMainCommand(),
            "tp-up" to TeleportUp(),
            "ender-toggle" to BecomeEnderman(),
            "protect" to ProtectArea(),
            "unprotect" to UnprotectArea(),
            "world" to ChangeWorlds(),
        )

        val explosiveArrow = ExplosiveArrow(this)
        val grapplingHook = GrapplingHook()
        val grenade = Grenade()
        val multiTool = MultiTool()
        val railgun = Railgun()
        val rapidFireBow = RapidFireBow()
        val teleportArrow = TeleportArrow()
        val customFuseTNT = CustomFuseTNT()

        val customItems = arrayOf<CustomItem>(
            explosiveArrow,
            grapplingHook,
            grenade,
            multiTool,
            railgun,
            rapidFireBow,
            teleportArrow,
            customFuseTNT
        )

        for (command in commands.entries) {
            val cmd = this.getCommand(command.key)
            if (cmd == null) {
                throw RuntimeException("Command " + command.key + " not found")
            }
            cmd.setExecutor(command.value)
        }

        val pluginManager = this.server.pluginManager

        val giveCustomItem = GiveCustomItem(this, customItems)
        val vanish = Vanish(this)
        val enchantAnything = EnchantAnything(this)
        val prankCommand = PrankCommand(this)
        ductTape = DuctTape(this)

        giveCustomItem.register()
        vanish.register()
        enchantAnything.register()
        prankCommand.register()
        ductTape.register()

        pluginManager.registerEvents(vanish, this)
        pluginManager.registerEvents(ductTape, this)

        pluginManager.registerEvents(explosiveArrow, this)
        pluginManager.registerEvents(grapplingHook, this)
        pluginManager.registerEvents(grenade, this)
        pluginManager.registerEvents(multiTool, this)
        pluginManager.registerEvents(railgun, this)
        pluginManager.registerEvents(rapidFireBow, this)
        pluginManager.registerEvents(teleportArrow, this)
        pluginManager.registerEvents(customFuseTNT, this)
    }

    private fun registerEvents(listeners: Array<Listener> = arrayOf()) {
        val pluginManager = this.server.pluginManager

        val listeners = arrayOf<Listener>(
            *listeners,
            DeathMessages(),
            JoinMessage(),
            BedMessage(),
            Moving(),
            Inventory(),
            ArrowListener(),
            BlockListener(),
            EntityListener(),
        )

        for (listener in listeners) {
            pluginManager.registerEvents(listener, this)
        }
    }

    private fun registerRecipes() {
        val recipes: Array<ShapedRecipe?> = arrayOf<ShapedRecipe?>(
            ExplosiveArrow(this).getRecipe(),
        )

        for (recipe in recipes) {
            Bukkit.addRecipe(recipe)
        }
    }

    override fun onDisable() {
        ProtectedAreas.saveAreas()

        saveTapedPlayers()

        //        for (Player player : getServer().getOnlinePlayers()) {
//            if (!defaultWorlds.contains(player.getWorld())) {
//                SandboxJoinCommand.sandboxedPlayers.get(player.getUniqueId()).revertPlayerState();
//            }
//        }

//        for (World world : getServer().getWorlds()) {
//            if (!defaultWorlds.contains(world)) {
//                Bukkit.unloadWorld(world, false);
//                try {
//                    FileUtils.deleteDirectory(world.getWorldFolder());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
    }

    private fun saveTapedPlayers() {
        val file = getConfig()
        ductTape.tapedPlayers.forEach { (uuid: UUID?, unmuteTime: LocalDateTime?) ->
            file.set(
                "taped-players.$uuid",
                unmuteTime.toString()
            )
        }
        saveConfig()
    }

    private fun loadTapedPlayers() {
        val file = getConfig()
        val tapedPlayersSection = file.getConfigurationSection("taped-players")
        if (tapedPlayersSection != null) {
            for (key in tapedPlayersSection.getKeys(false)) {
                val uuid = UUID.fromString(key)
                val unmuteTime =
                    LocalDateTime.parse(Objects.requireNonNull<String?>(tapedPlayersSection.getString(key)))
                ductTape.tapedPlayers.put(uuid, unmuteTime)
            }
        }
    }

    companion object {
        private lateinit var plugin: JavaPlugin

        @JvmField
        var defaultWorlds: MutableList<World?>? = null

        @JvmStatic
        fun getPlugin(): JavaPlugin {
            return plugin
        }
    }
}
