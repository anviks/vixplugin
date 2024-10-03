package com.github.anviks.vixplugin

import com.github.anviks.vixplugin.commands.CustomCommand
import com.github.anviks.vixplugin.custom_items.CustomCraftableItem
import com.github.anviks.vixplugin.custom_items.CustomItem
import com.github.anviks.vixplugin.util.PDCManager
import com.jeff_media.armorequipevent.ArmorEquipEvent
import com.jeff_media.customblockdata.CustomBlockData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import java.util.UUID
import kotlin.reflect.KClass

val json = Json {
    prettyPrint = true
    serializersModule = SerializersModule {
        contextual(UUID::class, UUIDSerializer)
    }
}

class VixPlugin : JavaPlugin() {

    private val dependencyRegistry = DependencyRegistry()
    private val instanceCache = mutableMapOf<Class<*>, Any>()
    private val pluginData = loadPluginState()

    override fun onEnable() {
        CustomBlockData.registerListener(this)
        ArmorEquipEvent.registerListener(this)

        PDCManager.init(this)

        dependencyRegistry.register<Plugin> { this }
        dependencyRegistry.register<JavaPlugin> { this }  // CommandAPICommand requires JavaPlugin
        dependencyRegistry.register<PluginState> { pluginData }

        val customItems = createChildrenOf<CustomItem>()

        dependencyRegistry.register { customItems }

        val customCommands = createChildrenOf<CustomCommand>()
        val customListeners = createChildrenOf<Listener>()
        val craftableItems = createChildrenOf<CustomCraftableItem>()

        customCommands.forEach { it.register() }
        customListeners.forEach { server.pluginManager.registerEvents(it, this) }
        craftableItems.forEach { Bukkit.addRecipe(it.getRecipe()) }

        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            pluginData.protectedEntities.forEach {
                it.value.removeIf { server.getEntity(it) == null }
                if (it.value.isEmpty()) pluginData.protectedEntities.remove(it.key)
            }
        }, 100, 100)
    }

    override fun onDisable() {
        savePluginState()
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

    private fun savePluginState() {
        val file = dataFolder.resolve("plugin_state.json")
        if (!file.exists()) file.createNewFile()
        file.writeText(json.encodeToString(pluginData))
    }

    private fun loadPluginState(): PluginState {
        val file = dataFolder.resolve("plugin_state.json")
        if (!file.exists()) return PluginState()
        return json.decodeFromString<PluginState>(file.readText())
    }
}
