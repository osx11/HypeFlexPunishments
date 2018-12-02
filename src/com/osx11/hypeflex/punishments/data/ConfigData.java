package com.osx11.hypeflex.punishments.data;

import com.osx11.hypeflex.punishments.Main;

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

    private static int coolDownKick;
    private static int coolDownBan;
    private static int coolDownBanIP;
    private static int coolDownTempban;
    private static int coolDownMute;
    private static int coolDownMuteIP;
    private static int coolDownTempmute;

    public void setConfigData() {
        try {
            MySQL_login = plugin.getConfig().getString("MySQL.login");
            MySQL_password = plugin.getConfig().getString("MySQL.password");
            MySQL_url = plugin.getConfig().getString("MySQL.url");

            coolDownKick = plugin.getConfig().getInt("cooldowns.kick");
            coolDownBan = plugin.getConfig().getInt("cooldowns.ban");
            coolDownBanIP = plugin.getConfig().getInt("cooldowns.banip");
            coolDownTempban = plugin.getConfig().getInt("cooldowns.tempban");
            coolDownMute = plugin.getConfig().getInt("cooldowns.mute");
            coolDownMuteIP = plugin.getConfig().getInt("cooldowns.muteip");
            coolDownTempmute = plugin.getConfig().getInt("cooldowns.tempmute");
        } catch (java.lang.NullPointerException e) { e.printStackTrace(); }
    }

// ---------------------------------------------------------------------------------------------------------------------
// Для доступа из вне

    public static String getMySQL_login() { return MySQL_login; }

    public static String getMySQL_password() { return MySQL_password; }

    public static String getMySQL_url() { return MySQL_url; }



    public static int getCoolDownKick() {
        return coolDownKick;
    }

    public static int getCoolDownBan() {
        return coolDownBan;
    }

    public static int getCoolDownBanIP() {
        return coolDownBanIP;
    }

    public static int getCoolDownTempban() {
        return coolDownTempban;
    }

    public static int getCoolDownMute() {
        return coolDownMute;
    }

    public static int getCoolDownMuteIP() {
        return coolDownMuteIP;
    }

    public static int getCoolDownTempmute() {
        return coolDownTempmute;
    }

// ---------------------------------------------------------------------------------------------------------------------

}