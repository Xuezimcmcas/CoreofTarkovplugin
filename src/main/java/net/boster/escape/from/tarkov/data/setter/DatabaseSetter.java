package net.boster.escape.from.tarkov.data.setter;

import lombok.RequiredArgsConstructor;
import net.boster.escape.from.tarkov.data.ConnectedDatabase;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public abstract class DatabaseSetter implements DataSetter {

    private final ConnectedDatabase db;

    @Override
    public void setUserData(@NotNull String uuid, int deaths, int kills) {
        db.setMySqlUserValue(uuid, deaths, kills);
    }

    @Override
    public int[] getUserData(@NotNull String uuid) {
        return db.getMySqlValue(uuid);
    }

    @Override
    public void deleteUser(@NotNull String uuid) {
        db.deleteUser(uuid);
    }
}
