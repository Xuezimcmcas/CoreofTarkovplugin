package net.boster.escape.from.tarkov.data;

import lombok.Getter;
import lombok.Setter;
import net.boster.escape.from.tarkov.EscapeFromTarkov;
import net.boster.escape.from.tarkov.data.database.DatabaseRunnable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {

    public static final Map<Player, PlayerData> hash = new HashMap<>();

    @Getter private final Player player;

    @Getter @Setter private int deaths = 0;
    @Getter @Setter private int kills = 0;

    public PlayerData(@NotNull Player p) {
        this.player = p;

        if(EscapeFromTarkov.getInstance().getDataSetter() != null) {
            new DatabaseRunnable().run(() -> {
                int[] data = EscapeFromTarkov.getInstance().getDataSetter().getUserData(p.getUniqueId().toString());
                if(data != null) {
                    deaths = data[0];
                    kills = data[1];
                }
            });
        }

        hash.put(p, this);
    }

    public static PlayerData get(@NotNull Player p) {
        return hash.get(p);
    }

    public void save() {
        if(EscapeFromTarkov.getInstance().getDataSetter() != null) {
            new DatabaseRunnable().run(() -> {
                EscapeFromTarkov.getInstance().getDataSetter().setUserData(player.getUniqueId().toString(), deaths, kills);
            });
        }
    }
}
