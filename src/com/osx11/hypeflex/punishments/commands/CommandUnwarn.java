package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandUnwarn implements CommandExecutor {

    public CommandUnwarn() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.unwarn")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean all = Utils.flagAll(args);
        boolean silent = Utils.flagSilent(args);
        args = Utils.removeFlags(args);

        if ((all && !sender.hasPermission("hfp.unwarn.all")) || (silent && !sender.hasPermission("hfp.unwarn.silent"))) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        String user_nick = args[0];

        User user = new User(user_nick);
        String user_uuid = user.getUUID();

        if (!all && args.length == 1) {
            for (String warn : MySQL.getWarnlist(user_uuid)) {
                sender.sendMessage(warn);
            }
            return true;
        }

        String warnID = null;
        if (!all) {
            warnID = args[1];
        }

        if (!user.hasActiveWarns()) {
            sender.sendMessage(MessagesData.getMSG_NoActiveWarns());
            return true;
        }

        if (!all && !user.hasWarn(warnID)) {
            for (String warn : MySQL.getWarnlist(user_uuid)) {
                sender.sendMessage(warn);
            }
            return true;
        }

        if (all) {
            user.removeAllWarns();
        } else {
            user.removeWarn(warnID);
        }

        Logging.INFO(MessagesData.getLogging_UnwarnLog(sender.getName(), user_nick));

        if (!silent) {
            Bukkit.broadcast(MessagesData.getLogging_UnwarnLog(sender.getName(), user_nick), "hfp.unwarn.notify");
        }

        return true;
    }

}
