package net.boster.escape.from.tarkov.data;

import java.sql.Connection;

public interface ConnectedDatabase {

    void createTableIfNotExists();
    void setMySqlUserValue(String key, int deaths, int kills);
    void deleteUser(String key);
    int[] getMySqlValue(String key);
    Connection getConnection();
    void closeConnection();
}
