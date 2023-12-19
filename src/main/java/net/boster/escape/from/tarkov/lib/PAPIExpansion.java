package net.boster.escape.from.tarkov.lib;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.boster.escape.from.tarkov.EscapeFromTarkov;
import net.boster.escape.from.tarkov.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "escape";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Bosternike";
    }

    @Override
    public @NotNull String getVersion() {
        return EscapeFromTarkov.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(@NotNull OfflinePlayer o, @NotNull String s) {
        Player p = Bukkit.getPlayer(o.getUniqueId());
        if(p == null) return null;

        if(s.equalsIgnoreCase("point")) {
            WGSupport.Region r = Utils.getNearestRegion(p.getLocation());
            if(r != null) {
                return r.getMin().getBlockX() + ", " + r.getMin().getBlockY() + ", " + r.getMin().getBlockZ();
            }
        } else if(s.equalsIgnoreCase("time_remaining")) {
            return Utils.timeFormat(EscapeFromTarkov.getInstance().getGameTime().getTimeRemaining());
        } else if(s.equalsIgnoreCase("name")) {
            WGSupport.Region r = Utils.getNearestRegion(p.getLocation());
            if(r != null) {
                return Utils.toColor(r.getEscapeName());
            }
        }

        return null;
    }
}
