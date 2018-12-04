package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.ConfigData;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.InvalidPunishReason;
import com.osx11.hypeflex.punishments.exceptions.PlayerNotFoundInDB;
import com.osx11.hypeflex.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandMuteIP implements CommandExecutor {

    private Main plugin;

    public CommandMuteIP(Main plugin) {
        this.plugin = plugin;
    }

    private CoolDown coolDown = new CoolDown();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.muteip")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean forceSpecified = false;
        if (args[0].equalsIgnoreCase("-f")) {
            if (User.hasPermission(sender, "hfp.mute.force")) {
                forceSpecified = true;
            } else {
                sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
                return true;
            }
        }

        String punishableNick;

        if (forceSpecified) {
            if (args.length < 2) { return false; }
            punishableNick = args[1];
        } else {
            punishableNick = args[0];
        }

        boolean found = false;
        String punishableIP = "";

        Pattern IPPattern = Pattern.compile("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))");
        Matcher matcher = IPPattern.matcher(punishableNick);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                punishableIP = matcher.group(1);
                found = true;
            }
        }

        if (!found) {
            try {
                punishableIP = User.getIP(punishableNick);
            } catch (PlayerNotFoundInDB e) {
                sender.sendMessage(e.getMessage());
                return true;
            }
        }

        GregorianCalendar gregorianCalendar = new GregorianCalendar();

        String reason = MessagesData.getReason_DefaultReason();
        final Player player = Bukkit.getPlayer(punishableNick);
        final String UUID = MySQL.getString("SELECT UUID FROM players WHERE nick=\"" + punishableNick + "\"", "UUID");
        final int cooldownConfig = ConfigData.getCoolDownMuteIP();
        final String date = String.valueOf(gregorianCalendar.get(Calendar.YEAR)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.MONTH)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.DATE));
        final String time = String.valueOf(gregorianCalendar.get(Calendar.HOUR)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.MINUTE)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.SECOND));

        // если игрок не найден
        if (!User.isOnline(player)) {
            if (!User.hasPermission(sender, "hfp.mute.offline")) {
                sender.sendMessage(MessagesData.getMSG_PlayerIsOffline());
                return true;
            }
            if (!forceSpecified) {
                if (UUID == null) { // если игрока нет вообще (по uuid в таблице)
                    sender.sendMessage(MessagesData.getMSG_IPNotFound(punishableIP));
                    return true;
                }
            }
        }

        // если у человека иммунитет
        if (User.hasPermission(punishableNick, "hfp.mute.exempt") && sender instanceof Player) {
            sender.sendMessage(MessagesData.getMSG_Exempt(punishableNick));
            return true;
        }

        // проверяем активно ли кд
        if (sender instanceof Player) {
            if (coolDown.hasCoolDown(Bukkit.getPlayer(sender.getName()), "muteip", cooldownConfig))
                return true;
        }

        // пишем причину
        try {
            if (forceSpecified) {
                if (args.length > 2) {
                    reason = Utils.GetFullReason(args, 2);
                }
            } else {
                if (args.length > 1) {
                    reason = Utils.GetFullReason(args, 1);
                }
            }
        } catch (InvalidPunishReason e) {
            sender.sendMessage(e.getMessage());
            return true;
        }

        // отправляем мсг, если онлайн
        if (User.isOnline(player)) {
            player.sendMessage(MessagesData.getReason_MuteIPReasonFormat(reason));
        }

        // добавляем в бд
        if (User.isMutedIP(punishableIP)) {
            if (User.hasPermission(sender, "hfp.mute.override")) {
                MySQL.insert("UPDATE mutesIP SET reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + sender.getName() + "\" WHERE IP=\"" + punishableIP + "\"");
            } else {
                sender.sendMessage(MessagesData.getMSG_CannotOverride());
                return true;
            }
        } else {
            MySQL.insert("INSERT INTO mutesIP SET IP=\"" + punishableIP + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + sender.getName() + "\"");
        }

        // логируем
        Logging.INFO(MessagesData.getLogging_MuteIPLog(sender.getName(), punishableIP, reason));

        // бродкастим
        Bukkit.broadcast(MessagesData.getLogging_MuteIPLog(sender.getName(), punishableIP, reason), "hfp.muteip.notify");

        return true;
    }

}
