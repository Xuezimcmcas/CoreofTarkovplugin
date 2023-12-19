package net.boster.escape.from.tarkov.counter;

import lombok.Getter;
import lombok.Setter;
import net.boster.escape.from.tarkov.EscapeFromTarkov;
import net.boster.escape.from.tarkov.lib.WGSupport;
import net.boster.escape.from.tarkov.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WaitTimeCounter extends GameCounter {

    public static final Map<Player, WaitTimeCounter> map = new HashMap<>();

    private int time;
    private final Player p;
    @Getter @Setter @Nullable private WGSupport.Region region;
    private BossBar bar;

    public WaitTimeCounter(@NotNull EscapeFromTarkov plugin, @NotNull Player p) {
        super(plugin);
        this.p = p;
        this.time = plugin.TIME_TO_ESCAPE;
        map.put(p, this);
    }

    public static @NotNull WaitTimeCounter get(@NotNull Player p) {
        WaitTimeCounter c = map.get(p);
        if(c == null) {
            return new WaitTimeCounter(EscapeFromTarkov.getInstance(), p);
        }

        return c;
    }

    @Override
    public void start() {
        stop();

        if(plugin.enterSound != null) {
            plugin.enterSound.play(p.getLocation());
        }
        p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.escapeAreaEntered")));
        String s = Utils.toColor(plugin.getConfig().getString("Settings.BossBar"));
        setBossBar(s);

        task = new BukkitRunnable() {

            @Override
            public void run() {
                if(!p.isOnline()) {
                    stop();
                    return;
                }

                time--;
                setBossBar(s);

                if(time <= 0) {
                    plugin.win(p);
                    stop();
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    private void setBossBar(String s) {
        if(bar == null) {
            bar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
            bar.addPlayer(p);
        }
        bar.setTitle(s.replace("%time%", "" + time));
        bar.setProgress((double) time / (double) plugin.TIME_TO_ESCAPE);
    }

    public void stop() {
        if(isStarted()) {
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.escapeAreaLeft")));
        }
        super.stop();
        time = plugin.TIME_TO_ESCAPE;
        if(bar != null) {
            bar.removePlayer(p);
            bar = null;
        }
    }
}
