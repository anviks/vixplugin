package me.captainpotatoaim.myplugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class AdventureHelper {

    public static Component createAlternatingColoredText(String text, NamedTextColor... colors) {
        TextComponent.Builder builder = Component.text();
        int colorCount = colors.length;

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            NamedTextColor color = colors[i % colorCount];
            builder.append(Component.text(currentChar, color));
        }

        return builder.build();
    }
}
