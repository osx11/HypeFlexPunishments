package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandBanlistIP implements CommandExecutor {

    public CommandBanlistIP() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.banlist")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length != 0) { return false; }

        if (MySQL.getBanListIP().length == 0) {
            sender.sendMessage(MessagesData.getMSG_NoActiveBans());
            return true;
        }

        for (String ban : MySQL.getBanListIP()) {
            sender.sendMessage(ban);
        }

        return true;
    }

}
