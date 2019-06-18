package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.InvalidPunishReason;
import com.osx11.hypeflex.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBan implements CommandExecutor {

    public CommandBan() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.ban")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean force = Utils.flagForce(args);
        boolean silent = Utils.flagSilent(args);
        args = Utils.removeFlags(args);

        if ((force && !sender.hasPermission("hfp.ban.force")) || (silent && !sender.hasPermission("hfp.ban.silent"))) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        String user_nickname = args[0];
        User user = new User(user_nickname);

        // если игрок не найден
        if (!user.isOnline()) {
            if (!sender.hasPermission("hfp.ban.offline")) {
                sender.sendMessage(MessagesData.getMSG_PlayerIsOffline(user_nickname));
                return true;
            }
            if (user.getUUID() == null) { // если игрока нет вообще (по uuid в таблице)
                sender.sendMessage(MessagesData.getMSG_PlayerNotFound(user_nickname));
                return true;
            }
        }

        // если у игрока иммунитет
        if (user.hasExempt("ban") && sender instanceof Player && !force) {
            sender.sendMessage(MessagesData.getMSG_Exempt(user_nickname));
            return true;
        }

        // проверяем активно ли кд у сендера
        if (sender instanceof Player && new User(sender.getName()).updateCooldown("ban")) {
            return true;
        }

        String reason = MessagesData.getReason_DefaultReason();
        // пишем причину
        if (args.length > 1) {
            try {
                reason = Utils.GetFullReason(args, 1);
            } catch (InvalidPunishReason e) {
                sender.sendMessage(e.getMessage());
                return true;
            }
        }

        // добавляем в бд
        if (user.isBanned() && !sender.hasPermission("hfp.ban.override")) {
            sender.sendMessage(MessagesData.getMSG_CannotOverride());
            return true;
        }

        user.ban(reason, sender.getName());

        // логируем
        Logging.INFO(MessagesData.getLogging_BanLog(sender.getName(), user_nickname, reason));

        // бродкастим
        if (!silent) {
            Bukkit.broadcast(MessagesData.getLogging_BanLog(sender.getName(), user_nickname, reason), "hfp.ban.notify");
        }

        return true;
    }

}
