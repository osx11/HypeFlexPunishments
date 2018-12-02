package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandUnban implements CommandExecutor {

    private Main plugin;

    public CommandUnban(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.unban")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        String punishableNick = args[0];

        // проверяем, что игрок забанен
        if (!MySQL.stringIsExist("bans", "nick", punishableNick)) {
            sender.sendMessage(MessagesData.getMSG_PlayerNotFound(punishableNick));
            return true;
        }

        // удаляем из бд
        MySQL.insert("DELETE FROM bans WHERE nick=\"" + punishableNick + "\"");

        // логируем
        Logging.INFO(MessagesData.getLogging_UnbanLog(sender.getName(), punishableNick));

        // бродкастим
        Bukkit.broadcastMessage(MessagesData.getLogging_UnbanLog(sender.getName(), punishableNick));

        return true;
    }

}
