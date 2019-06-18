package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.User;
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

import java.util.Arrays;

public class CommandTempban implements CommandExecutor {

    public CommandTempban() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.tempban")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 2) { return false; }

        boolean force = Utils.flagForce(args);
        boolean silent = Utils.flagSilent(args);
        args = Utils.removeFlags(args);

        if ((force && !sender.hasPermission("hfp.ban.force")) || (silent && !sender.hasPermission("hfp.ban.silent"))) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        String user_nick = args[0];
        String punishTime = args[1];

        User user = new User(user_nick);

        // если игрок не найден
        if (!user.isOnline()) {
            if (!sender.hasPermission("hfp.ban.offline")) {
                sender.sendMessage(MessagesData.getMSG_PlayerIsOffline(user_nick));
                return true;
            }
            if (user.getUUID() == null) { // если не форс и игрока нет вообще (по uuid в таблице)
                sender.sendMessage(MessagesData.getMSG_PlayerNotFound(user_nick));
                return true;
            }
        }

        // если у человека иммунитет
        if (user.hasExempt("ban") && sender instanceof Player && !force) {
            sender.sendMessage(MessagesData.getMSG_Exempt(user_nick));
            return true;
        }

        // проверяем активно ли кд
        if (sender instanceof Player && new User(sender.getName()).updateCooldown("tempban")) {
            return true;
        }

        // пишем причину
        String reason = MessagesData.getReason_DefaultReason();
        try {
            if (args.length > 2) {
                reason = Utils.GetFullReason(args, 2);
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

        // добавляем в бд
        if (user.isBanned() && !sender.hasPermission("hfp.ban.override")) {
            sender.sendMessage(MessagesData.getMSG_CannotOverride());
            return true;
        }

        String expire = Millis2Date.convertMillisToDate((System.currentTimeMillis() + (punishTimeSeconds * 1000)));

        user.ban(reason, sender.getName(), punishTimeString, punishTimeSeconds, expire);

        // логируем
        Logging.INFO(MessagesData.getLogging_TempbanLog(sender.getName(), user_nick, punishTimeString, reason));

        // бродкастим
        if (!silent) {
            Bukkit.broadcast(MessagesData.getLogging_TempbanLog(sender.getName(), user_nick, punishTimeString, reason), "hfp.ban.notify");
        }

        return true;
    }

}
