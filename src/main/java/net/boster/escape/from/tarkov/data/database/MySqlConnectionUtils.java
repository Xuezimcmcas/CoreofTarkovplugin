package net.boster.escape.from.tarkov.data.database;

import lombok.RequiredArgsConstructor;
import net.boster.escape.from.tarkov.EscapeFromTarkov;
import net.boster.escape.from.tarkov.utils.log.LogType;
import org.jetbrains.annotations.NotNull;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MySqlConnectionUtils extends SQLDatabase {

    @NotNull private final String host;
    private final int port;
    @NotNull private final String database;
    @NotNull private final String user;
    @NotNull private final String password;

    private ScheduledFuture<?> antiTimeOut;

    public synchronized boolean connect() {
        try {
            if(connection != null && !connection.isClosed()) {
                return false;
            }
            EscapeFromTarkov.getInstance().log("Connecting to database...", LogType.INFO);
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
            createTableIfNotExists();
            startAntiTimeOut();
            EscapeFromTarkov.getInstance().log("Database connection done!", LogType.FINE);
            return true;
        } catch (SQLException | IllegalArgumentException e) {
            EscapeFromTarkov.getInstance().log("Could not connect database!", LogType.ERROR);
            return false;
        }
    }

    public void startAntiTimeOut() {
        ScheduledExecutorService e = Executors.newScheduledThreadPool(1);

        e.scheduleAtFixedRate(() -> {
            try {
                setMySqlUserValueSync("BOSTER-PLUGIN-ANTI-TIME-OUT", 0, 0);
                deleteUser("BOSTER-PLUGIN-ANTI-TIME-OUT");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 1, 1, TimeUnit.HOURS);
    }

    public void stopAntiTimeOut() {
        if(antiTimeOut != null) {
            antiTimeOut.cancel(true);
            antiTimeOut = null;
        }
    }

    public void closeConnection() {
        stopAntiTimeOut();
        super.closeConnection();
    }
}
