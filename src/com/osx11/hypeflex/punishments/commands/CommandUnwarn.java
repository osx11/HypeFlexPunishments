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

public class CommandUnwarn implements CommandExecutor {

    private Main plugin;

    public CommandUnwarn(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.unwarn")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean allSpecified = false;
        if (args[0].equalsIgnoreCase("-a")) {
            if (User.hasPermission(sender, "hfp.unwarn.all")) {
                allSpecified = true;
            } else {
                sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
                return true;
            }
        }

        String punishableNick = args[0];
        if (allSpecified) {
            punishableNick = args[1];
        }


        if (!allSpecified) {
            if (args.length == 1) {
                sender.sendMessage("here is warnlist for " + punishableNick);
                for (int i = 0; i < MySQL.getWarnlist(punishableNick).length; i++) {
                    sender.sendMessage(MySQL.getWarnlist(punishableNick)[i]);
                }
                return true;
            }
        }

        String warnID = null;
        if (!allSpecified) {
            warnID = args[1];
        }

        if (!MySQL.stringIsExist("warns", "nick", punishableNick)) {
            sender.sendMessage(MessagesData.getMSG_NoActiveWarns());
            return true;
        }

        if (!allSpecified) {
            if (MySQL.getString("SELECT warnID FROM warns WHERE nick=\"" + punishableNick + "\" AND warnID=\"" + warnID + "\"", "warnID") == null) {
                sender.sendMessage(MessagesData.getMSG_WarnIDNotFound());
                for (int i = 0; i < MySQL.getWarnlist(punishableNick).length; i++) {
                    sender.sendMessage(MySQL.getWarnlist(punishableNick)[i]);
                }
                return true;
            }
        }

        if (allSpecified) {
            MySQL.insert("DELETE FROM warns WHERE nick=\"" + punishableNick + "\"");
        } else {
            MySQL.insert("DELETE FROM warns WHERE nick=\"" + punishableNick + "\" AND warnID=\"" + warnID + "\"");
        }

        Logging.INFO(MessagesData.getLogging_UnwarnLog(sender.getName(), punishableNick));

        Bukkit.broadcast(MessagesData.getLogging_UnwarnLog(sender.getName(), punishableNick), "hfp.unwarn.notify");

        return true;
    }

}
