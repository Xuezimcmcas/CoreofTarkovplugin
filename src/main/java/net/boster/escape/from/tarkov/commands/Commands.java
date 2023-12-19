package net.boster.escape.from.tarkov.commands;

import net.boster.escape.from.tarkov.EscapeFromTarkov;
import net.boster.escape.from.tarkov.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("escape.command.use")) {
            sender.sendMessage(Utils.toColor(EscapeFromTarkov.getInstance().getConfig().getString("Messages.noPermission")));
            return false;
        }

        if(args.length == 0) {
            sendHelp(sender);
            return false;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            EscapeFromTarkov.getInstance().reload();
            sender.sendMessage(Utils.toColor(EscapeFromTarkov.getInstance().getConfig().getString("Messages.reload")));
            return true;
        } else if(args[0].equalsIgnoreCase("start")) {
            EscapeFromTarkov.getInstance().start();
            sender.sendMessage(Utils.toColor(EscapeFromTarkov.getInstance().getConfig().getString("Messages.started")));
            return true;
        } else if(args[0].equalsIgnoreCase("stop")) {
            EscapeFromTarkov.getInstance().end();
            sender.sendMessage(Utils.toColor(EscapeFromTarkov.getInstance().getConfig().getString("Messages.ended")));
            return true;
        } else {
            sendHelp(sender);
            return false;
        }
    }

    private void sendHelp(CommandSender sender) {
        for(String s : EscapeFromTarkov.getInstance().getConfig().getStringList("Messages.help")) {
            sender.sendMessage(Utils.toColor(s));
        }
    }
}
