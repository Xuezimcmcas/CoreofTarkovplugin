package net.boster.escape.from.tarkov.counter;

import lombok.Getter;
import lombok.Setter;
import net.boster.escape.from.tarkov.EscapeFromTarkov;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class GameTimeCounter extends GameCounter {

    @Getter @Setter protected int timeRemaining = 0;

    public GameTimeCounter(@NotNull EscapeFromTarkov plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        stop();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                timeRemaining--;

                if(timeRemaining <= 0) {
                    plugin.end();
                    stop();
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }
}
