package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.utils.Millis2Date;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class CommandPinfo implements CommandExecutor {

    private Main plugin;

    public CommandPinfo(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.pinfo")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length != 1) { return false; }

        final String punishableNick = args[0];

        if (!MySQL.stringIsExist("players", "nick", punishableNick)) {
            sender.sendMessage(MessagesData.getMSG_PlayerNotFound(punishableNick));
            return true;
        }

        final String punishableIP = MySQL.getString("SELECT IP FROM players WHERE nick=\"" + punishableNick + "\"", "IP");
        final String stateTrue = MessagesData.getPINFO_stateYes();
        final String stateFalse = MessagesData.getPINFO_stateNo();
        final String timePermanent = MessagesData.getPINFO_timePermanent();

        if (User.isOnline(Bukkit.getPlayer(punishableNick))) {
            sender.sendMessage(MessagesData.getPINFO_InfoAbout(punishableNick, MessagesData.getPINFO_isOnline()));
        } else {
            sender.sendMessage(MessagesData.getPINFO_InfoAbout(punishableNick, MessagesData.getPINFO_isOffline()));
        }
        sender.sendMessage("  " + MessagesData.getPINFO_IP(punishableIP));
        if (MySQL.stringIsExist("bansIP", "IP", punishableIP)) {
            final String banIPReason = MySQL.getString("SELECT reason FROM bansIP WHERE IP=\"" + punishableIP + "\"", "reason");
            final String banIPIssuedBy = MySQL.getString("SELECT issuedBy FROM bansIP WHERE IP=\"" + punishableIP + "\"", "issuedBy");
            sender.sendMessage("  " + MessagesData.getPINFO_IPIsBanned(stateTrue));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishReason(banIPReason));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishInitiator(banIPIssuedBy));
        } else {
            sender.sendMessage("  " + MessagesData.getPINFO_IPIsBanned(stateFalse));
        }
        if (MySQL.stringIsExist("mutesIP", "IP", punishableIP)) {
            final String muteIPReason = MySQL.getString("SELECT reason FROM mutesIP WHERE IP=\"" + punishableIP + "\"", "reason");
            final String muteIPIssuedBy = MySQL.getString("SELECT issuedBy FROM mutesIP WHERE IP=\"" + punishableIP + "\"", "issuedBy");
            sender.sendMessage("  " + MessagesData.getPINFO_IPIsMuted(stateTrue));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishReason(muteIPReason));
            sender.sendMessage("    " + MessagesData.getPINFO_PunishInitiator(muteIPIssuedBy));
        } else {
            sender.sendMessage("  " + MessagesData.getPINFO_IPIsMuted(stateFalse));
        }
        if (MySQL.stringIsExist("bans", "nick", punishableNick)) {
            final String banPunishTimeString = MySQL.getString("SELECT punishTimeString FROM bans WHERE nick=\"" + punishableNick + "\"", "punishTimeString");
            final String banReason = MySQL.getString("SELECT reason FROM bans WHERE nick=\"" + punishableNick + "\"", "reason");
            final String banIssuedBy = MySQL.getString("SELECT issuedBy FROM bans WHERE nick=\"" + punishableNick + "\"", "issuedBy");
            final long expire = MySQL.getLong("SELECT expire FROM bans WHERE nick=\"" + punishableNick + "\"", "expire");
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
        if (MySQL.stringIsExist("mutes", "nick", punishableNick)) {
            final String mutePunishTimeString = MySQL.getString("SELECT punishTimeString FROM mutes WHERE nick=\"" + punishableNick + "\"", "punishTimeString");
            final String muteReason = MySQL.getString("SELECT reason FROM mutes WHERE nick=\"" + punishableNick + "\"", "reason");
            final String muteIssuedBy = MySQL.getString("SELECT issuedBy FROM mutes WHERE nick=\"" + punishableNick + "\"", "issuedBy");
            final long expire = MySQL.getLong("SELECT expire FROM mutes WHERE nick=\"" + punishableNick + "\"", "expire");
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

        return true;
    }

}
