package net.boster.escape.from.tarkov.data.setter;

import net.boster.escape.from.tarkov.data.ConnectedDatabase;
import org.jetbrains.annotations.NotNull;

public class MySqlSetter extends DatabaseSetter {

    public MySqlSetter(ConnectedDatabase db) {
        super(db);
    }

    @Override
    public @NotNull String getName() {
        return "MySql";
    }
}
