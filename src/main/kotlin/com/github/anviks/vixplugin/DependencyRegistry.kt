package com.github.anviks.vixplugin

import kotlin.reflect.KClass

class DependencyRegistry {
    private val dependencies = mutableMapOf<KClass<*>, () -> Any>()

    fun <T : Any> register(type: KClass<T>, provider: () -> T) {
        dependencies[type] = provider
    }

    inline fun <reified T : Any> register(noinline provider: () -> T) = register(T::class, provider)

    fun <T : Any> resolve(type: KClass<T>): T {
        val provider = dependencies[type] ?: throw IllegalArgumentException("No provider registered for ${type.simpleName}")
        return provider() as T
    }

    inline fun <reified T : Any> resolve(): T = resolve(T::class)
}
