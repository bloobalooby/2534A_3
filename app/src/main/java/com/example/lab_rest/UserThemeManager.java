package com.example.lab_rest;

import android.graphics.Color;

/**
 * Utility class to manage theme-related settings such as background and text colors
 * based on selected theme (light or dark).
 */
public class UserThemeManager {

    // Constants representing available themes
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";

    /**
     * Returns the background color for the given theme.
     *
     * @param theme The selected theme (light or dark)
     * @return Corresponding background color
     */
    public static int getBackgroundColor(String theme) {
        switch (theme) {
            case THEME_DARK:
                return Color.parseColor("#333333"); // Dark gray for dark theme
            default:
                return Color.parseColor("#F5F5F5"); // Light gray for light theme
        }
    }

    /**
     * Returns the text color for the given theme.
     *
     * @param theme The selected theme (light or dark)
     * @return Corresponding text color
     */
    public static int getTextColor(String theme) {
        switch (theme) {
            case THEME_DARK:
                return Color.WHITE; // White text on dark background
            default:
                return Color.BLACK; // Black text on light background
        }
    }
}
