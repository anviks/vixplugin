package com.github.anviks.vixplugin.configuration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigDependent(vararg val configOption: ConfigOption)
