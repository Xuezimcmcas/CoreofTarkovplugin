package net.boster.escape.from.tarkov.lib;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.boster.escape.from.tarkov.EscapeFromTarkov;
import net.boster.escape.from.tarkov.utils.log.LogType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class WGSupport {

    private static boolean loaded;
    private static WorldGuardPlugin plugin;

    public static void load() {
        try {
            plugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
            loaded = true;
        } catch (Exception ignored) {}
    }

    public static Region getRegion(@NotNull World world, @NotNull String s, @NotNull String escapeName) {
        if(loaded) {
            ProtectedRegion rg = plugin.getRegionManager(world).getRegion(s);
            if(rg == null) {
                EscapeFromTarkov.getInstance().log("Could not find region with name: " + s, LogType.ERROR);
                return null;
            }

            BlockVector v = rg.getMinimumPoint();
            BlockVector m = rg.getMaximumPoint();
            return new Region(escapeName, rg.getId(), new Location(world, v.getX(), v.getY(), v.getZ()), new Location(world, m.getX(), m.getY(), m.getZ())) {
                @Override
                public boolean isIn(@NotNull Location loc) {
                    return rg.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                }
            };
        } else {
            EscapeFromTarkov.getInstance().log("Could not find WorldGuard plugin: " + s, LogType.ERROR);
        }

        return null;
    }

    @RequiredArgsConstructor
    public static abstract class Region {
        @Getter @NotNull private final String escapeName;
        @Getter @NotNull private final String name;
        @Getter @NotNull private final Location min;
        @Getter @NotNull private final Location max;

        public abstract boolean isIn(@NotNull Location loc);
    }
}
