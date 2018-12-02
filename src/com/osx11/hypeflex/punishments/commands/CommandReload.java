package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.data.MessagesData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CommandReload implements CommandExecutor {

    private Main plugin;

    public CommandReload(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 0) { return false; }

        File messagesFile = new File(plugin.getDataFolder() + File.separator + "messages" + File.separator + "messages_ru.yml");
        FileConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);


        sender.sendMessage(MessagesData.getMSG_ConfigReloaded());

        return true;
    }
}
