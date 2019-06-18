package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.*;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.PlayerNotFoundInDB;
import com.osx11.hypeflex.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnbanIP implements CommandExecutor {

    public CommandUnbanIP() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.unbanip")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        boolean silent = Utils.flagSilent(args);
        args = Utils.removeFlags(args);

        if (silent && !sender.hasPermission("hfp.unbanip.silent")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        String user_ip;

        if (Utils.isiP(args[0])) {
            user_ip = args[0];
        } else {
            try {
                user_ip = new User(args[0]).getIP();
            } catch (PlayerNotFoundInDB e) {
                sender.sendMessage(e.getMessage());
                return true;
            }
        }

        // проверяем активно ли кд
        if (sender instanceof Player && new User(sender.getName()).updateCooldown("unbanip")) {
            return true;
        }

        IPAddr ipAddr = new IPAddr(user_ip);

        if (!ipAddr.isBanned()) {
            sender.sendMessage(MessagesData.getMSG_IPNotFound(user_ip));
            return true;
        }

        ipAddr.unban();

        Logging.INFO(MessagesData.getLogging_UnbanIPLog(sender.getName(), user_ip));

        if (!silent) {
            Bukkit.broadcast(MessagesData.getLogging_UnbanIPLog(sender.getName(), user_ip), "hfp.unbanip.notify");
        }

        return true;
    }

}
