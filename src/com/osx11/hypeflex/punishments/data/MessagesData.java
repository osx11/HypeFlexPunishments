package com.osx11.hypeflex.punishments.data;

import com.osx11.hypeflex.punishments.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class MessagesData {

    private Main plugin;

    public MessagesData(Main plugin) {
        this.plugin = plugin;
    }

// ---------------------------------------------------------------------------------------------------------------------
// Инициализация переменных для локального доступа

    private static String prefix;

    private static String MSG_Error;
    private static String MSG_ConfigReloaded;
    private static String MSG_MySQL_ConnectionError;
    private static String MSG_PlayerIsOffline;
    private static String MSG_PlayerNotFound;
    private static String MSG_IPNotFound;
    private static String MSG_Cooldown;
    private static String MSG_InsufficientPermissions;
    private static String MSG_Exempt;
    private static String MSG_CannotOverride;
    private static String MSG_InvalidTimeIndentifier;
    private static String MSG_InvalidPunishReason;
    private static String MSG_NoActiveBans;
    private static String MSG_NoActiveWarns;
    private static String MSG_WarnIDNotFound;

    private static String Reason_KickReasonFormat;
    private static String Reason_BanReasonFormat;
    private static String Reason_BanIPReasonFormat;
    private static String Reason_TempbanReasonFormat;
    private static String Reason_MuteReasonFormat;
    private static String Reason_MuteIPReasonFormat;
    private static String Reason_TempmuteReasonFormat;
    private static String Reason_WarnReasonFormat;
    private static String Reason_DefaultReason;

    private static String Logging_KickLog;
    private static String Logging_BanLog;
    private static String Logging_BanIPLog;
    private static String Logging_TempbanLog;
    private static String Logging_MuteLog;
    private static String Logging_MuteIPLog;
    private static String Logging_TempmuteLog;
    private static String Logging_WarnLog;
    private static String Logging_UnbanLog;
    private static String Logging_UnbanIPLog;
    private static String Logging_UnmuteLog;
    private static String Logging_UnmuteIPLog;
    private static String Logging_UnwarnLog;

    private static String TimeIdentifier_Seconds;
    private static String TimeIdentifier_Minutes;
    private static String TimeIdentifier_Hours;
    private static String TimeIdentifier_Days;
    private static String TimeIdentifier_Weeks;
    private static String TimeIdentifier_Months;
    private static String TimeIdentifier_Years;

    public final void setMessagesData() {
        try {
            File messagesFile = new File(plugin.getDataFolder() + File.separator + "messages" + File.separator + "messages_ru.yml");
            FileConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);

            prefix = messages.getString("Prefix").replaceAll("&", "§");

            MSG_Error = messages.getString("Error").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_ConfigReloaded = messages.getString("ConfigReloaded").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_MySQL_ConnectionError = messages.getString("MySQL_ConnectionError").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_PlayerIsOffline = messages.getString("PlayerIsOffline").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_PlayerNotFound = messages.getString("PlayerNotFound").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_IPNotFound = messages.getString("IPNotFound").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_Cooldown = messages.getString("Cooldown").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_InsufficientPermissions = messages.getString("InsufficientPermissions").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_Exempt = messages.getString("Exempt").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_CannotOverride = messages.getString("CannotOverride").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_InvalidTimeIndentifier = messages.getString("InvalidTimeIndentifier").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_InvalidPunishReason = messages.getString("InvalidPunishReason").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_NoActiveBans = messages.getString("NoActiveBans").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_NoActiveWarns = messages.getString("NoActiveWarns").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            MSG_WarnIDNotFound = messages.getString("WarnIDNotFound").replaceAll("&", "§").replaceAll("%prefix%", prefix);

            Reason_KickReasonFormat = messages.getString("PunishReasons.KickReasonFormat").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Reason_BanReasonFormat = messages.getString("PunishReasons.BanReasonFormat").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Reason_BanIPReasonFormat = messages.getString("PunishReasons.BanIPReasonFormat").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Reason_TempbanReasonFormat = messages.getString("PunishReasons.TempbanReasonFormat").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Reason_MuteReasonFormat = messages.getString("PunishReasons.MuteReasonFormat").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Reason_MuteIPReasonFormat = messages.getString("PunishReasons.MuteIPReasonFormat").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Reason_TempmuteReasonFormat = messages.getString("PunishReasons.TempmuteReasonFormat").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Reason_WarnReasonFormat = messages.getString("PunishReasons.WarnReasonFormat").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Reason_DefaultReason = messages.getString("PunishReasons.DefaultReason").replaceAll("&", "§").replaceAll("%prefix%", prefix);

            Logging_KickLog = messages.getString("Logging.KickLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_BanLog = messages.getString("Logging.BanLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_BanIPLog = messages.getString("Logging.BanIPLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_TempbanLog = messages.getString("Logging.TempbanLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_MuteLog = messages.getString("Logging.MuteLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_MuteIPLog = messages.getString("Logging.MuteIPLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_TempmuteLog = messages.getString("Logging.TempmuteLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_WarnLog = messages.getString("Logging.WarnLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_UnbanLog = messages.getString("Logging.UnbanLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_UnbanIPLog = messages.getString("Logging.UnbanIPLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_UnmuteLog = messages.getString("Logging.UnmuteLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_UnmuteIPLog = messages.getString("Logging.UnmuteIPLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);
            Logging_UnwarnLog = messages.getString("Logging.UnwarnLog").replaceAll("&", "§").replaceAll("%prefix%", prefix);

            TimeIdentifier_Seconds = messages.getString("TimeIdentifiers.Seconds");
            TimeIdentifier_Minutes = messages.getString("TimeIdentifiers.Minutes");
            TimeIdentifier_Hours = messages.getString("TimeIdentifiers.Hours");
            TimeIdentifier_Days = messages.getString("TimeIdentifiers.Days");
            TimeIdentifier_Weeks = messages.getString("TimeIdentifiers.Weeks");
            TimeIdentifier_Months = messages.getString("TimeIdentifiers.Months");
            TimeIdentifier_Years = messages.getString("TimeIdentifiers.Years");
        } catch (java.lang.NullPointerException e) { e.printStackTrace(); }
    }

// ---------------------------------------------------------------------------------------------------------------------
// Для доступа из вне

    public static String getMSG_Error() {
        return MSG_Error;
    }

    public static String getMSG_ConfigReloaded() {
        return MSG_ConfigReloaded;
    }

    public static String getMSG_MySQL_ConnectionError() {
        return MSG_MySQL_ConnectionError;
    }

    public static String getMSG_PlayerIsOffline() {
        return MSG_PlayerIsOffline;
    }

    public static String getMSG_PlayerNotFound(String player) {
        return MSG_PlayerNotFound.replaceAll("%player%", player);
    }

    public static String getMSG_IPNotFound(String IP) {
        return MSG_IPNotFound.replaceAll("%ip%", IP);
    }

    public static String getMSG_Cooldown(String cooldown) {
        return MSG_Cooldown.replaceAll("%cooldown%", cooldown);
    }

    public static String getMSG_InsufficientPermissions() {
        return MSG_InsufficientPermissions;
    }

    public static String getMSG_Exempt(String player) {
        return MSG_Exempt.replaceAll("%player%", player);
    }

    public static String getMSG_CannotOverride() {
        return MSG_CannotOverride;
    }

    public static String getMSG_InvalidTimeIndentifier(String value) {
        return MSG_InvalidTimeIndentifier.replaceAll("%value%", value);
    }

    public static String getMSG_InvalidPunishReason() {
        return MSG_InvalidPunishReason;
    }

    public static String getMSG_NoActiveBans() {
        return MSG_NoActiveBans;
    }

    public static String getMSG_NoActiveWarns() {
        return MSG_NoActiveWarns;
    }

    public static String getMSG_WarnIDNotFound() {
        return MSG_WarnIDNotFound;
    }



    public static String getReason_KickReasonFormat(String reason) {
        return Reason_KickReasonFormat.replaceAll("%reason%", reason);
    }

    public static String getReason_BanReasonFormat(String reason) {
        return Reason_BanReasonFormat.replaceAll("%reason%", reason);
    }

    public static String getReason_BanIPReasonFormat(String reason) {
        return Reason_BanIPReasonFormat.replaceAll("%reason%", reason);
    }

    public static String getReason_TempbanReasonFormat(String reason, String punishTimeString, String expire) {
        return Reason_TempbanReasonFormat.replaceAll("%reason%", reason).replaceAll("%punishTime%", punishTimeString).replaceAll("%expire%", expire);
    }

    public static String getReason_MuteReasonFormat(String reason) {
        return Reason_MuteReasonFormat.replaceAll("%reason%", reason);
    }

    public static String getReason_MuteIPReasonFormat(String reason) {
        return Reason_MuteIPReasonFormat.replaceAll("%reason%", reason);
    }

    public static String getReason_TempmuteReasonFormat(String reason, String punishTimeString, String expire) {
        return Reason_TempmuteReasonFormat.replaceAll("%reason%", reason).replaceAll("%punishTime%", punishTimeString).replaceAll("%expire%", expire);
    }

    public static String getReason_WarnReasonFormat(String reason) {
        return Reason_WarnReasonFormat.replaceAll("%reason%", reason);
    }

    public static String getReason_DefaultReason() {
        return Reason_DefaultReason;
    }



    public static String getLogging_KickLog(String executor, String punishableNick, String reason) {
        return Logging_KickLog.replaceAll("%executor%", executor).replaceAll("%punishableNick%", punishableNick).replaceAll("%reason%", reason);
    }

    public static String getLogging_BanLog(String executor, String punishableNick, String reason) {
        return Logging_BanLog.replaceAll("%executor%", executor).replaceAll("%punishableNick%", punishableNick).replaceAll("%reason%", reason);
    }

    public static String getLogging_BanIPLog(String executor, String punishableIP, String reason) {
        return Logging_BanIPLog.replaceAll("%executor%", executor).replaceAll("%punishableIP%", punishableIP).replaceAll("%reason%", reason);
    }

    public static String getLogging_TempbanLog(String executor, String punishableNick, String punishTimeString, String reason) {
        return Logging_TempbanLog.replaceAll("%executor%", executor).replaceAll("%punishableNick%", punishableNick).replaceAll("%punishTime%", punishTimeString).replaceAll("%reason%", reason);
    }

    public static String getLogging_MuteLog(String executor, String punishableNick, String reason) {
        return Logging_MuteLog.replaceAll("%executor%", executor).replaceAll("%punishableNick%", punishableNick).replaceAll("%reason%", reason);
    }

    public static String getLogging_MuteIPLog(String executor, String punishableIP, String reason) {
        return Logging_MuteIPLog.replaceAll("%executor%", executor).replaceAll("%punishableIP%", punishableIP).replaceAll("%reason%", reason);
    }

    public static String getLogging_TempmuteLog(String executor, String punishableNick, String punishTimeString, String reason) {
        return Logging_TempmuteLog.replaceAll("%executor%", executor).replaceAll("%punishableNick%", punishableNick).replaceAll("%punishTime%", punishTimeString).replaceAll("%reason%", reason);
    }

    public static String getLogging_WarnLog(String executor, String punishableNick, String reason) {
        return Logging_WarnLog.replaceAll("%executor%", executor).replaceAll("%punishableNick%", punishableNick).replaceAll("%reason%", reason);
    }

    public static String getLogging_UnbanLog(String executor, String punishableNick) {
        return Logging_UnbanLog.replaceAll("%executor%", executor).replaceAll("%punishableNick%", punishableNick);
    }

    public static String getLogging_UnbanIPLog(String executor, String punishableIP) {
        return Logging_UnbanIPLog.replaceAll("%executor%", executor).replaceAll("%punishableIP%", punishableIP);
    }

    public static String getLogging_UnmuteLog(String executor, String punishableNick) {
        return Logging_UnmuteLog.replaceAll("%executor%", executor).replaceAll("%punishableNick%", punishableNick);
    }

    public static String getLogging_UnmuteIPLog(String executor, String punishableIP) {
        return Logging_UnmuteIPLog.replaceAll("%executor%", executor).replaceAll("%punishableIP%", punishableIP);
    }

    public static String getLogging_UnwarnLog(String executor, String punishableNick) {
        return Logging_UnwarnLog.replaceAll("%executor%", executor).replaceAll("%punishableNick%", punishableNick);
    }



    public static String getTimeIdentifier_Seconds() {
        return TimeIdentifier_Seconds;
    }

    public static String getTimeIdentifier_Minutes() {
        return TimeIdentifier_Minutes;
    }

    public static String getTimeIdentifier_Hours() {
        return TimeIdentifier_Hours;
    }

    public static String getTimeIdentifier_Days() {
        return TimeIdentifier_Days;
    }

    public static String getTimeIdentifier_Weeks() {
        return TimeIdentifier_Weeks;
    }

    public static String getTimeIdentifier_Months() {
        return TimeIdentifier_Months;
    }

    public static String getTimeIdentifier_Years() {
        return TimeIdentifier_Years;
    }

// ---------------------------------------------------------------------------------------------------------------------

}