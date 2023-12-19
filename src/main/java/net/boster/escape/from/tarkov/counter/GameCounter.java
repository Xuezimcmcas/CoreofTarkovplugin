package net.boster.escape.from.tarkov.counter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.boster.escape.from.tarkov.EscapeFromTarkov;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public abstract class GameCounter {

    @Getter @NotNull protected final EscapeFromTarkov plugin;
    @Getter @Nullable protected BukkitTask task;

    public abstract void start();

    public void stop() {
        if(task != null) {
            task.cancel();
            task = null;
        }
    }

    public boolean isStarted() {
        return task != null;
    }
}
