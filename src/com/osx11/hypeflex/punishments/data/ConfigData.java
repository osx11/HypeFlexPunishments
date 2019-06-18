package com.osx11.hypeflex.punishments.data;

import com.osx11.hypeflex.punishments.Main;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public final class ConfigData {

    private Main plugin;

    public ConfigData(Main plugin) {
        this.plugin = plugin;
    }

// ---------------------------------------------------------------------------------------------------------------------
// Инициализация переменных для локального доступа

    private static String MySQL_login;
    private static String MySQL_password;
    private static String MySQL_url;

    private static HashMap<String, Long> cooldowns = new HashMap<>();

    private static String[] warnsCountCommands;
    private static boolean Warns_AutoExecute;
    private static boolean Warns_DeleteAfterLast;
    private static HashMap<String, Boolean> onlineOnly = new HashMap<>();
    private static HashMap<String, String> commandsToExecute = new HashMap<>();

    public void setConfigData() {
        try {
            MySQL_login = plugin.getConfig().getString("MySQL.login");
            MySQL_password = plugin.getConfig().getString("MySQL.password");
            MySQL_url = plugin.getConfig().getString("MySQL.url");

            cooldowns.put("kick", plugin.getConfig().getLong("cooldowns.kick"));
            cooldowns.put("ban", plugin.getConfig().getLong("cooldowns.ban"));
            cooldowns.put("banip", plugin.getConfig().getLong("cooldowns.banip"));
            cooldowns.put("tempban", plugin.getConfig().getLong("cooldowns.tempban"));
            cooldowns.put("mute", plugin.getConfig().getLong("cooldowns.mute"));
            cooldowns.put("muteip", plugin.getConfig().getLong("cooldowns.muteip"));
            cooldowns.put("tempmute", plugin.getConfig().getLong("cooldowns.tempmute"));
            cooldowns.put("warn", plugin.getConfig().getLong("cooldowns.warn"));

            warnsCountCommands = plugin.getConfig().getConfigurationSection("warns.on_warn_count").getKeys(false).toArray(new String[plugin.getConfig().getConfigurationSection("warns.on_warn_count").getKeys(false).size()]);
            Warns_AutoExecute = plugin.getConfig().getBoolean("warns.command_auto_execute");
            Warns_DeleteAfterLast = plugin.getConfig().getBoolean("warns.delete_all_warnings_after_last_warn");

            for (String onCount : warnsCountCommands) {
                onlineOnly.put(onCount, plugin.getConfig().getBoolean("warns.on_warn_count." + onCount + ".if_player_is_online"));
                commandsToExecute.put(onCount, plugin.getConfig().getString("warns.on_warn_count." + onCount + ".command"));
            }

        } catch (java.lang.NullPointerException e) { e.printStackTrace(); }
    }

// ---------------------------------------------------------------------------------------------------------------------
// Для доступа из вне

    public static String getMySQL_login() { return MySQL_login; }
    public static String getMySQL_password() { return MySQL_password; }
    public static String getMySQL_url() { return MySQL_url; }

    public static long getCooldown(final String command) {
        return cooldowns.get(command);
    }

    public static String[] getWarnsCountCommands() {
        return warnsCountCommands;
    }
    public static boolean getWarns_AutoExecute() {
        return Warns_AutoExecute;
    }
    public static boolean getWarns_DeleteAfterLast() {
        return Warns_DeleteAfterLast;
    }
    public static boolean getOnlineOnly(String onCount) { return onlineOnly.get(onCount); }
    public static String getCommandToExecute(String player, String onCount) {
        return commandsToExecute.get(onCount).replaceAll("%player%", player).replaceAll("%warnsCount%", onCount);
    }

// ---------------------------------------------------------------------------------------------------------------------

}