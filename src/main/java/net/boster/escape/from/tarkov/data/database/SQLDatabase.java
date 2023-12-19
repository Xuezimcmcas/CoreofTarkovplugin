package net.boster.escape.from.tarkov.data.database;

import lombok.Getter;
import net.boster.escape.from.tarkov.data.ConnectedDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SQLDatabase implements ConnectedDatabase {

    @Getter protected Connection connection;

    public abstract boolean connect();

    public void createTableIfNotExists() {
        new DatabaseRunnable().run(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `users` (`uuid` VARCHAR(36), `deaths` INTEGER NOT NULL, `kills` INTEGER NOT NULL, PRIMARY KEY (`uuid`))");
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setMySqlUserValue(String key, int deaths, int kills) {
        new DatabaseRunnable().run(() -> {
            setMySqlUserValueSync(key, deaths, kills);
        });
    }

    protected void setMySqlUserValueSync(String key, int deaths, int kills) {
        try {
            if(getMySqlValue(key) != null) {
                PreparedStatement st = connection.prepareStatement("UPDATE `users` SET deaths = ?, kills = ? WHERE uuid = ?");
                st.setInt(1, deaths);
                st.setInt(2, kills);
                st.setString(3, key);
                st.executeUpdate();
                st.close();
            } else {
                PreparedStatement st = connection.prepareStatement("INSERT INTO `users` (uuid, deaths, kills) VALUES (?, ?, ?)");
                st.setString(1, key);
                st.setInt(2, deaths);
                st.setInt(3, kills);
                st.execute();
                st.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String key) {
        new DatabaseRunnable().run(() -> {
            try {
                PreparedStatement st = connection.prepareStatement("DELETE FROM `users` WHERE uuid = '" + key + "'");
                st.execute();
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public int[] getMySqlValue(String key) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users` WHERE uuid = '" + key + "'");
            ResultSet rs = statement.executeQuery();
            int[] i = null;
            if(rs.next()) {
                i = new int[]{rs.getInt(2), rs.getInt(3)};
            }
            rs.close();
            statement.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeConnection() {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
