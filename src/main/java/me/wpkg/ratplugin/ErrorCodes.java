package me.wpkg.ratplugin;

public class ErrorCodes
{
    public static String ok(String message)
    {
        return "[0]" + message;
    }
    public static String error(String message)
    {
        return "[1]" + message;
    }
}
