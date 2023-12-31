package net.boster.escape.from.tarkov.lib;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPISupport {

    private static boolean isLoaded = false;

    public static void load() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            new PAPIExpansion().register();
            isLoaded = true;
        } catch (NoClassDefFoundError | Exception ignored) {}
    }

    public static boolean isLoaded() {
        return isLoaded;
    }

    public static String setPlaceholders(OfflinePlayer p, String s) {
        return PlaceholderAPI.setPlaceholders(p, s);
    }
}
