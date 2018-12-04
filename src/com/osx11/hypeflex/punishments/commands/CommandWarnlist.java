package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandWarnlist implements CommandExecutor {

    private Main plugin;

    public CommandWarnlist(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.warnlist")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length != 1) { return false; }

        final String punishableNick = args[0];

        if (MySQL.getWarnlist(punishableNick).length == 0) {
            sender.sendMessage(MessagesData.getMSG_NoActiveWarns());
            return true;
        }

        for (int i = 0; i < MySQL.getWarnlist(punishableNick).length; i++) {
            Bukkit.broadcastMessage(MySQL.getWarnlist(punishableNick)[i]);
        }

        return true;
    }
}
