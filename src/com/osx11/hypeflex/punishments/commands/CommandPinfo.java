package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.PlayerNotFoundInDB;
import com.osx11.hypeflex.punishments.utils.Millis2Date;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class CommandPinfo implements CommandExecutor {

    public CommandPinfo() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.pinfo")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length != 1) { return false; }

        final String user_nick = args[0];

        if (!MySQL.stringIsExist("players", "nick", user_nick)) {
            sender.sendMessage(MessagesData.getMSG_PlayerNotFound(user_nick));
            return true;
        }

        User user = new User(user_nick);

        final String stateTrue = MessagesData.getPINFO_stateYes();
        final String stateFalse = MessagesData.getPINFO_stateNo();
        final String timePermanent = MessagesData.getPINFO_timePermanent();
        String user_ip = null;

        try { user_ip = user.getIP(); }
        catch (PlayerNotFoundInDB e) {}


        if (user.isOnline()) {
            sender.sendMessage(MessagesData.getPINFO_InfoAbout(user_nick, MessagesData.getPINFO_isOnline()));
        } else {
            sender.sendMessage(MessagesData.getPINFO_InfoAbout(user_nick, MessagesData.getPINFO_isOffline()));
        }
        sender.sendMessage("  " + MessagesData.getPINFO_IP(user_ip));
        if (MySQL.stringIsExist("bansIP", "IP", user_ip)) {
            final String banIPReason = MySQL.getString("SELECT reason FROM bansIP WHERE IP=\"" + user_ip + "\"", "reason");
            final String banIPIssuedBy = MySQL.getString("SELECT issuedBy FROM bansIP WHERE IP=\"" + user_ip + "\"", "issuedBy");
            sender.sendMessage("  " + MessagesData.getPINFO_IPIsBanned(stateTrue));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishReason(banIPReason));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishInitiator(banIPIssuedBy));
        } else {
            sender.sendMessage("  " + MessagesData.getPINFO_IPIsBanned(stateFalse));
        }
        if (MySQL.stringIsExist("mutesIP", "IP", user_ip)) {
            final String muteIPReason = MySQL.getString("SELECT reason FROM mutesIP WHERE IP=\"" + user_ip + "\"", "reason");
            final String muteIPIssuedBy = MySQL.getString("SELECT issuedBy FROM mutesIP WHERE IP=\"" + user_ip + "\"", "issuedBy");
            sender.sendMessage("  " + MessagesData.getPINFO_IPIsMuted(stateTrue));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishReason(muteIPReason));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishInitiator(muteIPIssuedBy));
        } else {
            sender.sendMessage("  " + MessagesData.getPINFO_IPIsMuted(stateFalse));
        }
        if (MySQL.stringIsExist("bans", "nick", user_nick)) {
            final String banPunishTimeString = MySQL.getString("SELECT punishTimeString FROM bans WHERE nick=\"" + user_nick + "\"", "punishTimeString");
            final String banReason = MySQL.getString("SELECT reason FROM bans WHERE nick=\"" + user_nick + "\"", "reason");
            final String banIssuedBy = MySQL.getString("SELECT issuedBy FROM bans WHERE nick=\"" + user_nick + "\"", "issuedBy");
            final long expire = MySQL.getLong("SELECT expire FROM bans WHERE nick=\"" + user_nick + "\"", "expire");
            sender.sendMessage("  " + MessagesData.getPINFO_isBanned(stateTrue));
            if (banPunishTimeString.equals("*permanent*")) {
                sender.sendMessage("    " + MessagesData.getPINFO_PunishTime(timePermanent));
            } else {
                sender.sendMessage("    " + MessagesData.getPINFO_PunishTime(banPunishTimeString));
                sender.sendMessage("      " + MessagesData.getPINFO_PunishExpire(Objects.toString(Millis2Date.convertMillisToDate(expire), null)));
            }
            sender.sendMessage("    " + MessagesData.getPINFO_PunishReason(banReason));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishInitiator(banIssuedBy));
        } else {
            sender.sendMessage("  " + MessagesData.getPINFO_isBanned(stateFalse));
        }
        if (MySQL.stringIsExist("mutes", "nick", user_nick)) {
            final String mutePunishTimeString = MySQL.getString("SELECT punishTimeString FROM mutes WHERE nick=\"" + user_nick + "\"", "punishTimeString");
            final String muteReason = MySQL.getString("SELECT reason FROM mutes WHERE nick=\"" + user_nick + "\"", "reason");
            final String muteIssuedBy = MySQL.getString("SELECT issuedBy FROM mutes WHERE nick=\"" + user_nick + "\"", "issuedBy");
            final long expire = MySQL.getLong("SELECT expire FROM mutes WHERE nick=\"" + user_nick + "\"", "expire");
            sender.sendMessage("  " + MessagesData.getPINFO_isMuted(stateTrue));
            if (mutePunishTimeString.equals("*permanent*")) {
                sender.sendMessage("    " + MessagesData.getPINFO_PunishTime(timePermanent));
            } else {
                sender.sendMessage("    " + MessagesData.getPINFO_PunishTime(mutePunishTimeString));
                sender.sendMessage("      " + MessagesData.getPINFO_PunishExpire(Objects.toString(Millis2Date.convertMillisToDate(expire), null)));
            }
            sender.sendMessage("    " + MessagesData.getPINFO_PunishReason(muteReason));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishInitiator(muteIssuedBy));
        } else {
            sender.sendMessage("  " + MessagesData.getPINFO_isMuted(stateFalse));
        }
        if (MySQL.stringIsExist("warns", "nick", user_nick)) {
            final int warnsCount = MySQL.getInt("SELECT COUNT(nick) FROM warns WHERE nick=\"" + user_nick + "\"", "COUNT(nick)");
            sender.sendMessage("  " + MessagesData.getPINFO_WarnsCount(String.valueOf(warnsCount)));
        } else {
            sender.sendMessage("  " + MessagesData.getPINFO_WarnsCount(stateFalse));
        }

        return true;
    }

}
