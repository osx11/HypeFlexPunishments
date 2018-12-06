package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CommandTest implements CommandExecutor {

    private Main plugin;

    public CommandTest(Main plugin) {
        this.plugin = plugin;
    }

    private CoolDown coolDown = new CoolDown();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        File playersFile = new File(plugin.getDataFolder() + File.separator + "players.yml");
        FileConfiguration players = YamlConfiguration.loadConfiguration(playersFile);

        for (Object p : players.getConfigurationSection("players").getKeys(false).toArray()) {
            System.out.println("Importing to DB... " + p + " : " + players.getString("players." + p + ".uuid"));
            MySQL.insert("INSERT INTO players SET IP=\"0.0.0.0\", nick=\"" + p + "\", UUID=\"" + players.getString("players." + p + ".uuid") + "\"");
        }

        System.out.println("Complete inserting to players!");

        for (Object p : players.getConfigurationSection("players").getKeys(false).toArray()) {
            System.out.println("Importing to DB... " + p + " : " + players.getString("players." + p + ".uuid"));
            MySQL.insert("INSERT INTO bans SET nick=\"" + p + "\", punishTimeString=\"*permanent*\", reason=\"test\", issuedDate=\"2018-12-06\", issuedTime=\"00:00:00\", issuedBy=\"CONSOLE\"");
        }

        System.out.println("Complete inserting to bans!");

        return true;
    }

}
