package net.boster.escape.from.tarkov;

import lombok.Getter;
import lombok.Setter;
import net.boster.escape.from.tarkov.commands.Commands;
import net.boster.escape.from.tarkov.counter.GameTimeCounter;
import net.boster.escape.from.tarkov.data.ConnectedDatabase;
import net.boster.escape.from.tarkov.data.PlayerData;
import net.boster.escape.from.tarkov.data.database.DatabaseRunnable;
import net.boster.escape.from.tarkov.data.database.MySqlConnectionUtils;
import net.boster.escape.from.tarkov.data.setter.DataSetter;
import net.boster.escape.from.tarkov.data.setter.MySqlSetter;
import net.boster.escape.from.tarkov.lib.PAPISupport;
import net.boster.escape.from.tarkov.lib.WGSupport;
import net.boster.escape.from.tarkov.listeners.PlayerListener;
import net.boster.escape.from.tarkov.utils.Utils;
import net.boster.escape.from.tarkov.utils.log.LogType;
import net.boster.escape.from.tarkov.utils.sound.BosterSound;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EscapeFromTarkov extends JavaPlugin implements PluginMessageListener {

    @Getter private static EscapeFromTarkov instance;

    public boolean started = false;

    @Getter private DataSetter dataSetter;
    private ConnectedDatabase connectedDatabase;

    public int MAX_GAME_TIME = 0;
    public int TIME_TO_ESCAPE = 0;
    public World world;
    public final List<WGSupport.Region> regions = new ArrayList<>();
    public String sendTo = "";
    @Nullable public BosterSound enterSound;
    @Getter @Setter @NotNull private GameTimeCounter gameTime = new GameTimeCounter(this);

    public int noPlayersAfterTime = 1;
    public String noPlayersAfterMessage;

    public void onEnable() {
        instance = this;

        String PREFIX = "\u00a76+\u00a7a---------------- \u00a7dEscapeFromTarkov \u00a7a------------------\u00a76+";

        DatabaseRunnable.enable();

        saveDefaultConfig();

        PAPISupport.load();
        WGSupport.load();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "bungeecord:main");
        getServer().getMessenger().registerIncomingPluginChannel(this, "bungeecord:main", this);
        getCommand("escape").setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getServer().getScheduler().runTaskLater(this, () -> {
            Bukkit.getConsoleSender().sendMessage(PREFIX);
            load();
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bEscapeFromTarkov\u00a7d] \u00a7fThe plugin has been \u00a7dEnabled\u00a7f!");
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bEscapeFromTarkov\u00a7d] \u00a7fPlugin creator: \u00a7dXuezimcmcas");
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bEscapeFromTarkov\u00a7d] \u00a7fPlugin version: \u00a7d" + getDescription().getVersion());
            Bukkit.getConsoleSender().sendMessage(PREFIX);
        }, 10);
    }

    public void onDisable() {
        DatabaseRunnable.disable();

        for(Player p : Bukkit.getOnlinePlayers()) {
            PlayerData d = PlayerData.get(p);
            if(d != null) {
                d.save();
            }
        }

        if(connectedDatabase != null) {
            connectedDatabase.closeConnection();
        }
        if(dataSetter != null) {
            dataSetter.onDisable();
        }

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    public void start() {
        if(started) return;

        started = true;
        gameTime.setTimeRemaining(MAX_GAME_TIME);
        gameTime.start();
    }

    public void win(@NotNull Player p) {
        connectToServer(p, sendTo);
    }

    private void endOnly() {
        if(!started) return;

        started = false;
        gameTime.stop();

        for(Player p : Bukkit.getOnlinePlayers()) {
            PlayerData d = PlayerData.get(p);
            if(d != null) {
                d.save();
            }

            p.setHealth(0);
        }
    }

    public void end() {
        if(!started) return;

        endOnly();
        Bukkit.shutdown();
    }

    public void reload() {
        endOnly();

        if(connectedDatabase != null) {
            connectedDatabase.closeConnection();
        }
        if(dataSetter != null) {
            dataSetter.onDisable();
        }

        reloadConfig();

        load();
    }

    private void load() {
        loadDataSetter();
        MAX_GAME_TIME = getConfig().getInt("Settings.GameTime");
        TIME_TO_ESCAPE = getConfig().getInt("Settings.WaitToEscape");
        gameTime.setTimeRemaining(MAX_GAME_TIME);
        sendTo = getConfig().getString("Settings.SendTo");
        enterSound = BosterSound.load(getConfig().getString("Settings.EnterSound"));

        world = Bukkit.getWorld(getConfig().getString("Settings.World"));
        ConfigurationSection c = getConfig().getConfigurationSection("Settings.Regions");
        if(c != null) {
            for(String r : c.getKeys(false)) {
                ConfigurationSection rg = c.getConfigurationSection(r);
                if(rg == null) continue;

                World w = Bukkit.getWorld(rg.getString("world"));
                if(w == null) continue;

                String name = rg.getString("region");
                if(name == null) continue;

                String escapeName = rg.getString("escapename");
                if(escapeName == null) continue;

                WGSupport.Region region = WGSupport.getRegion(w, name, escapeName);
                if(region != null) {
                    regions.add(region);
                }
            }
        }

        noPlayersAfterTime = getConfig().getInt("Settings.NoPlayersAfter.time");

        String na = "";
        for(String s : getConfig().getStringList("Settings.NoPlayersAfter.denyMessage")) {
            noPlayersAfterMessage += na + s;
            na = "\n";
        }
    }

    private void loadDataSetter() {
        if(connectedDatabase != null) {
            connectedDatabase.closeConnection();
        }

        dataSetter = loadDataSetter0();
    }

    private DataSetter loadDataSetter0() {
        String host = getConfig().getString("MySql.host", "");
        int port = getConfig().getInt("MySql.port", 3306);
        String user = getConfig().getString("MySql.user", "");
        String password = getConfig().getString("MySql.password", "");
        String db = getConfig().getString("MySql.database", "");

        MySqlConnectionUtils con = new MySqlConnectionUtils(host, port, db, user, password);
        if (con.connect()) {
            connectedDatabase = con;
            return new MySqlSetter(con);
        }

        return null;
    }

    public void sendToBungee(@NotNull Player p, String... args) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            for(String s : args) {
                out.writeUTF(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.sendPluginMessage(this, "bungeecord:main", b.toByteArray());
    }

    public void sendToBungee(String... args) {
        if(Bukkit.getOnlinePlayers().size() == 0) return;

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            for(String s : args) {
                out.writeUTF(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendPluginMessage(this, "bungeecord:main", b.toByteArray());
            return;
        }
    }

    public void connectToServer(@NotNull Player p, @NotNull String server) {
        sendToBungee(p, "Connect", server);
    }

    public void log(@NotNull String s, @NotNull LogType log) {
        Bukkit.getConsoleSender().sendMessage(log.getFormat() + log.getColor() + Utils.toColor(s));
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

    }
}
