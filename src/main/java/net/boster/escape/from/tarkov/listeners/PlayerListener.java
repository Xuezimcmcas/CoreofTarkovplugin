package net.boster.escape.from.tarkov.listeners;

import net.boster.escape.from.tarkov.EscapeFromTarkov;
import net.boster.escape.from.tarkov.counter.WaitTimeCounter;
import net.boster.escape.from.tarkov.data.PlayerData;
import net.boster.escape.from.tarkov.lib.WGSupport;
import net.boster.escape.from.tarkov.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent e) {
        if(EscapeFromTarkov.getInstance().started && EscapeFromTarkov.getInstance().getGameTime().getTimeRemaining() <= EscapeFromTarkov.getInstance().noPlayersAfterTime) {
            e.disallow(null, Utils.toColor(EscapeFromTarkov.getInstance().noPlayersAfterMessage));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        new PlayerData(e.getPlayer());
        EscapeFromTarkov.getInstance().start();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PlayerData d = PlayerData.get(p);
        if(d != null) {
            d.save();
        }
        WaitTimeCounter.get(p).stop();
        WaitTimeCounter.map.remove(p);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        PlayerData pp = PlayerData.get(p);
        pp.setDeaths(pp.getDeaths() + 1);
        if(p.getKiller() != null) {
            PlayerData dd = PlayerData.get(p.getKiller());
            dd.setKills(dd.getKills() + 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(EscapeFromTarkov.getInstance().regions.isEmpty()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                WaitTimeCounter c = WaitTimeCounter.get(p);
                if(c.getRegion() != null && c.getRegion().isIn(p.getLocation())) return;

                c.setRegion(null);

                for(WGSupport.Region region : EscapeFromTarkov.getInstance().regions) {
                    if(region.isIn(p.getLocation())) {
                        if (!c.isStarted()) {
                            c.start();
                        }
                        c.setRegion(region);
                        return;
                    }
                }

                if(c.getRegion() == null && c.isStarted()) {
                    c.stop();
                }
            }
        }.runTaskLater(EscapeFromTarkov.getInstance(), 1);
    }
}
