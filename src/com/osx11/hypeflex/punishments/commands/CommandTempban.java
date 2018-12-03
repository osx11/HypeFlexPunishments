package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.ConfigData;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.InvalidPunishReason;
import com.osx11.hypeflex.punishments.exceptions.InvalidTimeIdentifier;
import com.osx11.hypeflex.punishments.utils.DateUtils;
import com.osx11.hypeflex.punishments.utils.Millis2Date;
import com.osx11.hypeflex.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CommandTempban implements CommandExecutor {

    private Main plugin;

    public CommandTempban(Main plugin) {
        this.plugin = plugin;
    }

    private CoolDown coolDown = new CoolDown();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.tempban")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean forceSpecified = false;
        if (args[0].equalsIgnoreCase("-f")) {
            if (User.hasPermission(sender, "hfp.ban.force")) {
                forceSpecified = true;
            } else {
                sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            }
        }

        String punishableNick;
        String punishTime;

        if (forceSpecified) {
            if (args.length < 3) { return false; }
            punishableNick = args[1];
            punishTime = args[2];
        } else {
            if (args.length < 2) {return false; }
            punishableNick = args[0];
            punishTime = args[1];
        }

        GregorianCalendar gregorianCalendar = new GregorianCalendar();

        String reason = MessagesData.getReason_DefaultReason();
        Player player = Bukkit.getPlayer(punishableNick);
        String UUID = MySQL.getString("SELECT UUID FROM players WHERE nick=\"" + punishableNick + "\"", "UUID");
        int cooldownConfig = ConfigData.getCoolDownTempban();
        String date = String.valueOf(gregorianCalendar.get(Calendar.YEAR)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.MONTH)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.DATE));
        String time = String.valueOf(gregorianCalendar.get(Calendar.HOUR)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.MINUTE)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.SECOND));

        // если игрок не найден
        if (!User.isOnline(player)) {
            if (!User.hasPermission(sender, "hfp.ban.offline")) {
                sender.sendMessage(MessagesData.getMSG_PlayerIsOffline().replaceAll("%player%", punishableNick));
                return true;
            }
            if (!forceSpecified) {
                if (UUID == null) { // если игрока нет вообще (по uuid в таблице)
                    sender.sendMessage(MessagesData.getMSG_PlayerNotFound(punishableNick));
                    return true;
                }
            }
        }

        // если у человека иммунитет
        if (User.hasPermission(punishableNick, "hfp.ban.exempt") && sender instanceof Player) {
            sender.sendMessage(MessagesData.getMSG_Exempt(punishableNick));
            return true;
        }

        // проверяем активно ли кд
        if (sender instanceof Player) {
            if (coolDown.hasCoolDown(Bukkit.getPlayer(sender.getName()), punishableNick, "tempban", cooldownConfig))
                return true;
        }

        // пишем причину
        try {
            if (forceSpecified) {
                if (args.length > 3) {
                    reason = Utils.GetFullReason(args, 3);
                }
            } else {
                if (args.length > 2) {
                    reason = Utils.GetFullReason(args, 2);
                }
            }
        } catch (InvalidPunishReason e) {
            sender.sendMessage(e.getMessage());
            return true;
        }

        // переводим указанное исполнителем время в секунды и строковой формат
        String[] punishInfo;
        long punishTimeSeconds;
        String punishTimeString;

        try {
            punishInfo = DateUtils.punishInfo(punishTime);
        } catch (InvalidTimeIdentifier e) {
            sender.sendMessage(e.getMessage());
            return true;
        }

        punishTimeSeconds = Long.parseLong(punishInfo[0]);
        punishTimeString = punishInfo[1];

        // кикаем, если онлайн
        if (User.isOnline(player)) {
            User.kickUser("tempban", player, reason, punishTimeString, Millis2Date.convertMillisToDate((System.currentTimeMillis() + (punishTimeSeconds * 1000))));
        }

        // добавляем в бд
        if (User.isBanned(punishableNick)) {
            if (User.hasPermission(sender, "hfp.ban.override")) {
                MySQL.insert("UPDATE bans SET punishTimeString=\"" + punishTimeString + "\", punishTimeSeconds=\"" + punishTimeSeconds + "\", expire=\"" + (System.currentTimeMillis() + (punishTimeSeconds * 1000)) + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + sender.getName() + "\" WHERE nick=\"" + punishableNick + "\"");
            } else {
                sender.sendMessage(MessagesData.getMSG_CannotOverride());
                return true;
            }
        } else {
            MySQL.insert("INSERT INTO bans SET nick=\"" + punishableNick + "\", punishTimeString=\"" + punishTimeString + "\", punishTimeSeconds=\"" + punishTimeSeconds + "\", expire=\"" + (System.currentTimeMillis() + (punishTimeSeconds * 1000)) + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + sender.getName() + "\"");
        }

        // логируем
        Logging.INFO(MessagesData.getLogging_TempbanLog(sender.getName(), punishableNick, punishTimeString, reason));

        // бродкастим
        Bukkit.broadcastMessage(MessagesData.getLogging_TempbanLog(sender.getName(), punishableNick, punishTimeString, reason));

        return true;
    }

}
