package com.xs.stocksettings.utils;

import android.content.Context;

/**
 * Created by xs on 16-3-29.
 */
public class PrefUtils {

    private static final String SharedPreferencesConfigName = "com.xs.stocksettings_preferences";

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return context.getSharedPreferences(SharedPreferencesConfigName, Context.MODE_PRIVATE)
                .getBoolean(key, defaultValue);
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        context.getSharedPreferences(SharedPreferencesConfigName, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, value)
                .commit();
    }

    public static void saveString(Context context, String key, String string) {
        context.getSharedPreferences(SharedPreferencesConfigName, Context.MODE_PRIVATE)
                .edit()
                .putString(key, string)
                .commit();
    }
}
