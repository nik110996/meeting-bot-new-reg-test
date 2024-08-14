package ru.meetingbot;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Локализация с использованием статического ResourceBundle
 */
public class ResBundle {

    private static ResourceBundle bundle = ResourceBundle.getBundle("strings");

    /**
     * установить локаль
     */
    public static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle("strings", locale);
    }

    /**
     * взять значение по ключу
     */
    public static String getMessage(String key) {
        return bundle.getString(key);
    }
}
