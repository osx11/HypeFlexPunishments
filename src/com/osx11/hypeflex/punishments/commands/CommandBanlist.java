package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandBanlist implements CommandExecutor {

    private Main plugin;

    public CommandBanlist(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.banlist")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length != 0) { return false; }

        if (MySQL.getBanList().length == 0) {
            sender.sendMessage(MessagesData.getMSG_NoActiveBans());
            return true;
        }

        for (int i = 0; i < MySQL.getBanList().length; i++) {
            sender.sendMessage(MySQL.getBanList()[i]);
        }

        return true;
    }

}
