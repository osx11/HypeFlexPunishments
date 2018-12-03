package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.PlayerNotFoundInDB;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandUnmuteIP implements CommandExecutor {

    private Main plugin;

    public CommandUnmuteIP(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.unbanip")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        boolean found = false;
        String punishableNick = args[0];
        String punishableIP = "";

        Pattern IPPattern = Pattern.compile("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))");
        Matcher matcher = IPPattern.matcher(punishableNick);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                punishableIP = matcher.group(1);
                found = true;
            }
        }

        if (!found) {
            try {
                punishableIP = User.getIP(punishableNick);
            } catch (PlayerNotFoundInDB e) {
                sender.sendMessage(e.getMessage());
                return true;
            }
        }

        // проверяем, что игрок забанен
        if (!User.isMutedIP(punishableIP)) {
            sender.sendMessage(MessagesData.getMSG_IPNotFound(punishableIP));
            return true;
        }

        // удаляем из бд
        MySQL.insert("DELETE FROM mutesIP WHERE IP=\"" + punishableIP + "\"");

        // логируем
        Logging.INFO(MessagesData.getLogging_UnmuteIPLog(sender.getName(), punishableIP));

        // бродкастим
        Bukkit.broadcastMessage(MessagesData.getLogging_UnmuteIPLog(sender.getName(), punishableIP));

        return true;
    }

}
