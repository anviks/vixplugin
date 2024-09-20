package me.captainpotatoaim.myplugin.util

object StringHelper {
    fun camelCaseToSnakeCase(camelCase: String): String {
        return camelCase.replace("([a-z])([A-Z])".toRegex(), "$1_$2").lowercase()
    }

    fun camelCaseToKebabCase(camelCase: String): String {
        return camelCase.replace("([a-z])([A-Z])".toRegex(), "$1-$2").lowercase()
    }
}