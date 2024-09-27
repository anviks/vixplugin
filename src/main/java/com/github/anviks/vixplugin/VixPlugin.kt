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
import com.github.anviks.vixplugin.random_commands.protect_area.Area
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
import java.time.LocalDateTime
import java.util.Objects
import java.util.UUID
import kotlin.reflect.KClass

typealias DuctTapedPlayers = MutableMap<UUID, LocalDateTime>
typealias ProtectedAreas = MutableMap<String, Area>

class VixPlugin : JavaPlugin() {

    private val dependencyRegistry = DependencyRegistry()
    private val instanceCache = mutableMapOf<Class<*>, Any>()
    private val tapedPlayers = loadTapedPlayers()
    private val protectedAreas = loadAreas()

    override fun onEnable() {
        CustomBlockData.registerListener(this)
        PDCManager.init(this)
        val pluginManager = this.server.pluginManager

        dependencyRegistry.register<Plugin> { this }
        dependencyRegistry.register<JavaPlugin> { this }  // CommandAPICommand requires JavaPlugin
        dependencyRegistry.register<DuctTapedPlayers> { tapedPlayers }
        dependencyRegistry.register<ProtectedAreas> { protectedAreas }

        val customItems = createChildrenOf<CustomItem>()

        dependencyRegistry.register { customItems }

        val customCommands = createChildrenOf<CustomCommand>()
        val customListeners = createChildrenOf<Listener>()
        val craftableItems = createChildrenOf<CustomCraftableItem>()

        customCommands.forEach { it.register() }
        customListeners.forEach { pluginManager.registerEvents(it, this) }
        craftableItems.forEach { Bukkit.addRecipe(it.getRecipe()) }

        registerCommands()

        loadAreas()
    }

    override fun onDisable() {
        saveAreas()
        saveTapedPlayers()
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

    private fun saveTapedPlayers() {
        val file = getConfig()
        tapedPlayers.forEach { (uuid: UUID?, unmuteTime: LocalDateTime?) ->
            file.set(
                "taped-players.$uuid",
                unmuteTime.toString()
            )
        }
        saveConfig()
    }

    private fun loadTapedPlayers(): DuctTapedPlayers {
        val file = getConfig()
        val tapedPlayersSection = file.getConfigurationSection("taped-players")
        val tapedPlayers = HashMap<UUID, LocalDateTime>()

        if (tapedPlayersSection != null) {
            for (key in tapedPlayersSection.getKeys(false)) {
                val uuid = UUID.fromString(key)
                val unmuteTime =
                    LocalDateTime.parse(Objects.requireNonNull<String?>(tapedPlayersSection.getString(key)))
                tapedPlayers.put(uuid, unmuteTime)
            }
        }

        return tapedPlayers
    }

    private fun saveAreas() {
        val file = dataFolder.resolve("protected-areas.json")
        file.writeText(Json.encodeToString(protectedAreas))
    }

    private fun loadAreas(): ProtectedAreas {
        val file = dataFolder.resolve("protected-areas.json")
        val areas = Json.decodeFromString<ProtectedAreas>(file.readText())
        return areas
    }
}
