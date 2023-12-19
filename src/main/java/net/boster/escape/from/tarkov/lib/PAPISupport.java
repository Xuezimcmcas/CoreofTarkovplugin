package net.boster.escape.from.tarkov.lib;

import org.bukkit.OfflinePlayer;

public class PAPISupport {

    public static void load() {
        PlaceholderAPISupport.load();
    }

    public static String setPlaceholders(OfflinePlayer p, String s) {
        if (PlaceholderAPISupport.isLoaded()) {
            return PlaceholderAPISupport.setPlaceholders(p, s);
        } else {
            return s;
        }
    }
}
