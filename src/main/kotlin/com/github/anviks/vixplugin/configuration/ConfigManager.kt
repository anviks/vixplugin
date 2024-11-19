package com.github.anviks.vixplugin.configuration

import org.bukkit.plugin.Plugin


class ConfigManager(private val plugin: Plugin) {

    val grenadesExplodeIndividually: Boolean
        get() = plugin.config.getBoolean(ConfigOption.GRENADES_EXPLODE_INDIVIDUALLY.path, false)

    fun reloadConfig() {
        plugin.reloadConfig()
    }
}