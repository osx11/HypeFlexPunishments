package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.ConfigData;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.InvalidPunishReason;
import com.osx11.hypeflex.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWarn implements CommandExecutor {

    public CommandWarn() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.warn")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean force = Utils.flagForce(args);
        boolean silent = Utils.flagSilent(args);
        args = Utils.removeFlags(args);

        if ((force && !sender.hasPermission("hfp.warn.force")) || (silent && !sender.hasPermission("hfp.warn.silent"))) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        String user_nick = args[0];

        User user = new User(user_nick);

        // если игрок не найден
        if (!user.isOnline()) {
            if (!sender.hasPermission("hfp.warn.offline")) {
                sender.sendMessage(MessagesData.getMSG_PlayerIsOffline(user_nick));
                return true;
            }
            if (user.getUUID() == null) { // если игрока нет вообще (по uuid в таблице)
                sender.sendMessage(MessagesData.getMSG_PlayerNotFound(user_nick));
                return true;
            }
        }

        // если у человека иммунитет
        if (user.hasExempt("warn") && sender instanceof Player && !force) {
            sender.sendMessage(MessagesData.getMSG_Exempt(user_nick));
            return true;
        }

        // проверяем активно ли кд
        if (sender instanceof Player && new User(sender.getName()).updateCooldown("warn")) {
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

        // отправляем мсг, если онлайн
        if (user.isOnline()) {
            user.sendMessage(MessagesData.getReason_WarnReasonFormat(reason));
        }

        int warnsCount = user.getWarnsCount() + 1;

        // добавляем в бд
        user.addWarn(reason, sender.getName());

        // логируем
        Logging.INFO(MessagesData.getLogging_WarnLog(sender.getName(), user_nick, reason));

        // бродкастим
        if (!silent) {
            Bukkit.broadcast(MessagesData.getLogging_WarnLog(sender.getName(), user_nick, reason), "hfp.warn.notify");
        }

        //--------------------------------------------------------------------------------------------------------------
        // выполнение автоматических команд
        String[] warnsCountCommand = ConfigData.getWarnsCountCommands();
        boolean isLastWarn = false;

        if (ConfigData.getWarns_AutoExecute()) {
            for (String onCount : warnsCountCommand) {
                if (warnsCount == Integer.parseInt(onCount)) {
                    if (warnsCount == Integer.parseInt(warnsCountCommand[warnsCountCommand.length - 1])) {
                        isLastWarn = true;
                    }

                    boolean onlineOnly = ConfigData.getOnlineOnly(onCount);
                    String executeCommand = ConfigData.getCommandToExecute(user_nick, onCount);

                    if (!user.isOnline()) {
                        if (!onlineOnly) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executeCommand);
                        }
                    } else {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executeCommand);
                    }
                }
            }
            if (ConfigData.getWarns_DeleteAfterLast()) {
                if (isLastWarn) {
                    user.removeAllWarns();
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        return true;
    }

}
