package net.boster.escape.from.tarkov.utils.log;

import lombok.Getter;

public enum LogType {

    FINE("§d[§bEscapeFromTarkov§d] §7[§aFINE§7] ", "§a"),
    INFO("§d[§bEscapeFromTarkov§d] §7[§9INFO§7] ", "§9"),
    WARNING("§d[§bEscapeFromTarkov§d] §7[§cWARNING§7] ", "§c"),
    ERROR("§d[§bEscapeFromTarkov§d] §7[§4ERROR§7] ", "§4");

    @Getter private final String format;
    @Getter private final String color;

    LogType(String s, String color) {
        this.format = s;
        this.color = color;
    }
}
