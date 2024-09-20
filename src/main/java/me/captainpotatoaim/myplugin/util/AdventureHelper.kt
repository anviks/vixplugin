package me.captainpotatoaim.myplugin.util;

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

object AdventureHelper {
    fun createAlternatingColoredText(text: String, vararg colors: NamedTextColor?): Component {
        val builder = Component.text()
        val colorCount = colors.size

        for (i in text.indices) {
            val currentChar = text[i]
            val color = colors[i % colorCount]
            builder.append(Component.text(currentChar, color))
        }

        return builder.build()
    }
}
