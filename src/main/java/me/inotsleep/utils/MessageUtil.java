package me.inotsleep.utils;

public class MessageUtil {

    public static String parsePlaceholders(String str, String ...strings) {
        for (int i = 0; i < strings.length; i++) {
            str = str.replace("{"+i+"}", strings[i]);
        }
        return str;
    }
}
