package com.example.lab_rest;

import android.graphics.Color;

public class UserThemeManager {
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_BLUE = "blue";

    public static int getBackgroundColor(String theme) {
        switch (theme) {
            case THEME_DARK: return Color.parseColor("#333333");
            case THEME_BLUE: return Color.parseColor("#2196F3");
            default: return Color.parseColor("#F5F5F5"); // Light
        }
    }

    public static int getTextColor(String theme) {
        switch (theme) {
            case THEME_DARK:
            case THEME_BLUE:
                return Color.WHITE;
            default:
                return Color.BLACK;
        }
    }
}