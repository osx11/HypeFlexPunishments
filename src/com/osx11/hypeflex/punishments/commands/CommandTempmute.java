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
import java.util.Objects;

public class CommandTempmute implements CommandExecutor {

    private Main plugin;

    public CommandTempmute(Main plugin) {
        this.plugin = plugin;
    }

    private CoolDown coolDown = new CoolDown();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.tempmute")) {
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
        final Player player = Bukkit.getPlayer(punishableNick);
        final String UUID = MySQL.getString("SELECT UUID FROM players WHERE nick=\"" + punishableNick + "\"", "UUID");
        final int cooldownConfig = ConfigData.getCoolDownTempmute();
        final String date = String.valueOf(gregorianCalendar.get(Calendar.YEAR)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.MONTH)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.DATE));
        final String time = String.valueOf(gregorianCalendar.get(Calendar.HOUR)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.MINUTE)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.SECOND));

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
        if (User.hasPermission(punishableNick, "hfp.mute.exempt") && sender instanceof Player) {
            sender.sendMessage(MessagesData.getMSG_Exempt(punishableNick));
            return true;
        }

        // проверяем активно ли кд
        if (sender instanceof Player) {
            if (coolDown.hasCoolDown(Bukkit.getPlayer(sender.getName()), "tempmute", cooldownConfig))
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

        String expire = Objects.toString(((((System.currentTimeMillis() + (punishTimeSeconds * 1000)) - System.currentTimeMillis()) / 1000L) / 60), null);

        // отправляем сообщение
        if (User.isOnline(player)) {
            player.sendMessage(MessagesData.getReason_TempmuteReasonFormat(reason, punishTimeString, expire));
        }

        // добавляем в бд

        if (User.isMuted(punishableNick)) {
            if (User.hasPermission(sender, "hfp.ban.override")) {
                MySQL.insert("UPDATE mutes SET punishTimeString=\"" + punishTimeString + "\", punishTimeSeconds=\"" + punishTimeSeconds + "\", expire=\"" + (System.currentTimeMillis() + (punishTimeSeconds * 1000)) + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + sender.getName() + "\" WHERE nick=\"" + punishableNick + "\"");
            } else {
                sender.sendMessage(MessagesData.getMSG_CannotOverride());
                return true;
            }
        } else {
            MySQL.insert("INSERT INTO mutes SET nick=\"" + punishableNick + "\", punishTimeString=\"" + punishTimeString + "\", punishTimeSeconds=\"" + punishTimeSeconds + "\", expire=\"" + (System.currentTimeMillis() + (punishTimeSeconds * 1000)) + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + sender.getName() + "\"");
        }

        // логируем
        Logging.INFO(MessagesData.getLogging_TempmuteLog(sender.getName(), punishableNick, punishTimeString, reason));

        // бродкастим
        Bukkit.broadcastMessage(MessagesData.getLogging_TempmuteLog(sender.getName(), punishableNick, punishTimeString, reason));

        return true;
    }

}
