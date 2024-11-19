package com.github.anviks.vixplugin.util


fun String.camelToSnake(): String {
    return this.replace("([a-z])([A-Z])".toRegex(), "$1_$2").lowercase()
}

fun String.camelToKebab(): String {
    return this.replace("([a-z])([A-Z])".toRegex(), "$1-$2").lowercase()
}

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}
