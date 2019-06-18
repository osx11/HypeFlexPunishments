package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandWarnlist implements CommandExecutor {

    public CommandWarnlist() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.warnlist")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length != 1) { return false; }

        String user_nick = args[0];
        String UUID = new User(user_nick).getUUID();

        if (MySQL.getWarnlist(UUID).length == 0) {
            sender.sendMessage(MessagesData.getMSG_NoActiveWarns());
            return true;
        }

        for (String warn : MySQL.getWarnlist(UUID)) {
            sender.sendMessage(warn);
        }

        return true;
    }
}
