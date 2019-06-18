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

public class CommandKick implements CommandExecutor {

    public CommandKick() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.kick")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean force = Utils.flagForce(args);
        boolean silent = Utils.flagSilent(args);
        args = Utils.removeFlags(args);

        if ((force && !sender.hasPermission("hfp.kick.force")) || (silent && !sender.hasPermission("hfp.kick.silent"))) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        final String user_nick = args[0];
        final String date = DateUtils.getCurrentDate();
        final String time = DateUtils.getCurrentTime();

        User user = new User(user_nick);

        // если игрок оффлайн
        if (!user.isOnline()) {
            sender.sendMessage(MessagesData.getMSG_PlayerNotFound(user_nick));
            return true;
        }

        // если у чела иммунитет
        if (user.hasExempt("kick") && sender instanceof Player && !force) {
            sender.sendMessage(MessagesData.getMSG_Exempt(user_nick));
            return true;
        }

        // проверяем на активное кд
        if (sender instanceof Player && new User(sender.getName()).updateCooldown("kick")) {
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

        // кикаем
        user.kickUser(reason, true, sender.getName());

        // логируем
        Logging.INFO(MessagesData.getLogging_KickLog(sender.getName(), user_nick, reason));

        // бродкастим
        if (!silent) {
            Bukkit.broadcast(MessagesData.getLogging_KickLog(sender.getName(), user_nick, reason), "hfp.kick.notify");
        }

        return true;
    }

}
