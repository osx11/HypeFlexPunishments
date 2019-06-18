package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.InvalidPunishReason;
import com.osx11.hypeflex.punishments.utils.DateUtils;
import com.osx11.hypeflex.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandMute implements CommandExecutor {

    public CommandMute() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.mute")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean force = Utils.flagForce(args);
        boolean silent = Utils.flagSilent(args);
        args = Utils.removeFlags(args);

        if ((force && !sender.hasPermission("hfp.mute.force")) || (silent && !sender.hasPermission("hfp.mute.silent"))) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        String user_nick = args[0];

        final String date = DateUtils.getCurrentDate();
        final String time = DateUtils.getCurrentTime();

        User user = new User(user_nick);

        // если игрок не найден
        if (!user.isOnline()) {
            if (!sender.hasPermission("hfp.mute.offline")) {
                sender.sendMessage(MessagesData.getMSG_PlayerIsOffline(user_nick));
                return true;
            }
            if (user.getUUID() == null) { // если не форс игрока нет вообще (по uuid в таблице)
                sender.sendMessage(MessagesData.getMSG_PlayerNotFound(user_nick));
                return true;
            }
        }

        // если у человека иммунитет
        if (user.hasExempt("mute") && sender instanceof Player && !force) {
            sender.sendMessage(MessagesData.getMSG_Exempt(user_nick));
            return true;
        }

        // проверяем активно ли кд
        if (sender instanceof Player && new User(sender.getName()).updateCooldown("mute")) {
            return true;
        }

        // пишем причину
        String reason = MessagesData.getReason_DefaultReason();
        try {
            if (args.length > 1) {
                reason = Utils.GetFullReason(args, 1);
            }
        } catch (InvalidPunishReason e) {
            sender.sendMessage(e.getMessage());
            return true;
        }

        // отправляем сообщение о муте
        if (user.isOnline()) {
            user.sendMessage(MessagesData.getReason_MuteReasonFormat(reason));
        }

        // добавляем в бд
        if (user.isMuted() && !sender.hasPermission("hfp.mute.override")) {
            sender.sendMessage(MessagesData.getMSG_CannotOverride());
            return true;
        }

        user.mute(reason, sender.getName());

        // логируем
        Logging.INFO(MessagesData.getLogging_MuteLog(sender.getName(), user_nick, reason));

        // бродкастим
        if (!silent) {
            Bukkit.broadcast(MessagesData.getLogging_MuteLog(sender.getName(), user_nick, reason), "hfp.mute.notify");
        }

        return true;
    }
}
