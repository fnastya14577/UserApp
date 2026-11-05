package com.example.userapp;

public class Transform {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";

    public static int parseIntOrDefault(String whatParse, int defaultValue) {
        try {
            return Integer.parseInt(whatParse);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Boolean StringNoNull(String string) {
        if (string == null) return false;
        else return string.length() != 0;
    }
}