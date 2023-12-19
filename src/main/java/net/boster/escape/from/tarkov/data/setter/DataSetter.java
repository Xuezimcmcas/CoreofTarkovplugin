package net.boster.escape.from.tarkov.data.setter;

import org.jetbrains.annotations.NotNull;

public interface DataSetter {

    @NotNull String getName();

    void setUserData(@NotNull String uuid, int deaths, int kills);

    int[] getUserData(@NotNull String uuid);

    void deleteUser(@NotNull String uuid);

    default void onDisable() {

    }
}
