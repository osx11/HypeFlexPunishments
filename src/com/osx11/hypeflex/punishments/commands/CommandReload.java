package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.data.MessagesData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandReload implements CommandExecutor {

    private Main plugin;

    public CommandReload(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 0) { return false; }


        plugin.reloadConfig();
        plugin.setConfigData();
        plugin.setMessagesData();

        sender.sendMessage(MessagesData.getMSG_ConfigReloaded());

        return true;
    }
}
