package net.boster.escape.from.tarkov.utils;

import net.boster.escape.from.tarkov.EscapeFromTarkov;
import net.boster.escape.from.tarkov.lib.WGSupport;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Utils {

    public static @NotNull String toColor(@NotNull String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static @NotNull String timeFormat(int time) {
        int ms = time / 60;
        int ss = time % 60;
        String m = ms > 0 ? (ms < 10 ? "0" : "") + ms : null;
        String s = (ss < 10 ? "0" : "") + ss;

        if(m != null) {
            return EscapeFromTarkov.getInstance().getConfig().getString("Settings.TimeFormat.Minutes").replace("%minutes%", m).replace("%seconds%", s);
        } else {
            return EscapeFromTarkov.getInstance().getConfig().getString("Settings.TimeFormat.Seconds").replace("%seconds%", s);
        }
    }

    public static @Nullable WGSupport.Region getNearestRegion(@NotNull Location loc) {
        if(EscapeFromTarkov.getInstance().regions.isEmpty()) return null;

        WGSupport.Region rg = EscapeFromTarkov.getInstance().regions.get(0);
        double dist = rg.getMin().distance(loc);

        for(WGSupport.Region r : EscapeFromTarkov.getInstance().regions) {
            double d = r.getMin().distance(loc);
            if(d < dist) {
                rg = r;
                dist = d;
            }
        }

        return rg;
    }
}
