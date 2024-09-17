package me.captainpotatoaim.myplugin.util;

public class StringHelper {
    public static String camelCaseToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public static String camelCaseToKebabCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
