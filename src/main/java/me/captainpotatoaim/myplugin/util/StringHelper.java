package me.captainpotatoaim.myplugin.util;

public class StringHelper {
    public static String camelCaseToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
