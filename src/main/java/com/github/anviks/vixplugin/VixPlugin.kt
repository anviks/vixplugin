package com.github.anviks.vixplugin

import com.github.anviks.vixplugin.custom_items.CustomCraftableItem
import com.github.anviks.vixplugin.custom_items.CustomItem
import com.github.anviks.vixplugin.enderman.BecomeEnderman
import com.github.anviks.vixplugin.random_commands.ChangeWorlds
import com.github.anviks.vixplugin.random_commands.DogCommand
import com.github.anviks.vixplugin.random_commands.EnderChestCommand
import com.github.anviks.vixplugin.random_commands.FlightCommand
import com.github.anviks.vixplugin.random_commands.Freeze
import com.github.anviks.vixplugin.random_commands.GodMode
import com.github.anviks.vixplugin.random_commands.InventoryCommand
import com.github.anviks.vixplugin.random_commands.LaunchCommand
import com.github.anviks.vixplugin.random_commands.SlapCommand
import com.github.anviks.vixplugin.random_commands.TeleportUp
import com.github.anviks.vixplugin.random_commands.UnFreeze
import com.github.anviks.vixplugin.random_commands.ZoomCommand
import com.github.anviks.vixplugin.util.PDCManager
import com.jeff_media.customblockdata.CustomBlockData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import java.lang.RuntimeException
import kotlin.reflect.KClass

val json = Json { prettyPrint = true }

class VixPlugin : JavaPlugin() {

    private val dependencyRegistry = DependencyRegistry()
    private val instanceCache = mutableMapOf<Class<*>, Any>()
    private val pluginData = loadPluginData()

    override fun onEnable() {
        CustomBlockData.registerListener(this)
        PDCManager.init(this)
        val pluginManager = this.server.pluginManager

        dependencyRegistry.register<Plugin> { this }
        dependencyRegistry.register<JavaPlugin> { this }  // CommandAPICommand requires JavaPlugin
        dependencyRegistry.register<PluginState> { pluginData }

        val customItems = createChildrenOf<CustomItem>()

        dependencyRegistry.register { customItems }

        val customCommands = createChildrenOf<CustomCommand>()
        val customListeners = createChildrenOf<Listener>()
        val craftableItems = createChildrenOf<CustomCraftableItem>()

        customCommands.forEach { it.register() }
        customListeners.forEach { pluginManager.registerEvents(it, this) }
        craftableItems.forEach { Bukkit.addRecipe(it.getRecipe()) }

        registerCommands()
    }

    override fun onDisable() {
        savePluginData()
    }

    private fun registerCommands() {
        val commands = mapOf(
            "loyalsquad" to DogCommand(this),
            "slap" to SlapCommand(),
            "fly" to FlightCommand(),
            "inventory" to InventoryCommand(),
            "echest" to EnderChestCommand(),
            "launch" to LaunchCommand(),
            "zoom" to ZoomCommand(this),
            "freeze" to Freeze(this),
            "unfreeze" to UnFreeze(),
            "god" to GodMode(),
            //  "sandbox" to SandboxMainCommand(),
            "tp-up" to TeleportUp(),
            "ender-toggle" to BecomeEnderman(),
            "world" to ChangeWorlds(),
        )

        commands.forEach {
            val cmd = this.getCommand(it.key) ?: throw RuntimeException("Command ${it.key} not found")
            cmd.setExecutor(it.value)
        }
    }

    private inline fun <reified T> createChildrenOf(): List<T> {
        val reflections = Reflections(VixPlugin::class.java.`package`.name)
        val childClasses = reflections.getSubTypesOf(T::class.java)

        return childClasses
            .filter { it.constructors.isNotEmpty() }
            .map { getOrCreateInstance(it) }
    }

    private inline fun <reified T> getOrCreateInstance(clazz: Class<out T>): T {
        // Check if the instance is already cached
        if (instanceCache.containsKey(clazz)) {
            return instanceCache[clazz] as T
        }

        // Get the primary constructor
        val constructor = clazz.kotlin.constructors.firstOrNull()
            ?: throw IllegalArgumentException("No constructor found for ${clazz.name}")

        // Resolve constructor parameters
        val params = constructor.parameters.map { param ->
            dependencyRegistry.resolve(param.type.classifier as KClass<*>)
        }

        val instance = constructor.call(*params.toTypedArray())
        instanceCache[clazz] = instance

        return instance
    }

    private fun savePluginData() {
        val file = dataFolder.resolve("plugin_state.json")
        if (!file.exists()) file.createNewFile()
        file.writeText(json.encodeToString(pluginData))
    }

    private fun loadPluginData(): PluginState {
        val file = dataFolder.resolve("plugin_state.json")
        if (!file.exists()) return PluginState()
        return json.decodeFromString<PluginState>(file.readText())
    }
}
