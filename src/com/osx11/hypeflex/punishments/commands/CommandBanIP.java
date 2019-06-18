package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.*;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.InvalidPunishReason;
import com.osx11.hypeflex.punishments.exceptions.PlayerNotFoundInDB;
import com.osx11.hypeflex.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBanIP implements CommandExecutor {

    public CommandBanIP() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.banip")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean force = Utils.flagForce(args);
        boolean silent = Utils.flagSilent(args);
        args = Utils.removeFlags(args);

        if ((force && !sender.hasPermission("hfp.banip.force")) || (silent && !sender.hasPermission("hfp.banip.silent"))) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        /*
         * Проверяем: при исполнении команды был указан IP адрес или ник игрока.
         * Если указан ник игрока, то берем его IP адрес из базы
         */
        String user_ip = null;

        if (Utils.isiP(args[0])) {
            user_ip = args[0];
        } else {
            try {
                user_ip = new User(args[0]).getIP();
            } catch (PlayerNotFoundInDB e) {
                if (!force) {
                    sender.sendMessage(e.getMessage());
                    return true;
                }
            }
        }

        // проверяем активно ли кд
        if (sender instanceof Player && new User(sender.getName()).updateCooldown("banip")) {
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

        IPAddr ipAddr = new IPAddr(user_ip);

        if (ipAddr.isBanned() && !sender.hasPermission("hfp.banip.override")) {
            sender.sendMessage(MessagesData.getMSG_CannotOverride());
            return true;
        }

        ipAddr.ban(reason, sender.getName());

        Logging.INFO(MessagesData.getLogging_BanIPLog(sender.getName(), user_ip, reason));

        if (!silent) {
            Bukkit.broadcast(MessagesData.getLogging_BanIPLog(sender.getName(), user_ip, reason), "hfp.banip.notify");
        }

        return true;
    }

}
