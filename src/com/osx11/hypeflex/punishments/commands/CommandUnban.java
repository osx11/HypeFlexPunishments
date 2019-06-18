package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.User;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandUnban implements CommandExecutor {

    public CommandUnban() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("hfp.unban")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        boolean silent = Utils.flagSilent(args);
        args = Utils.removeFlags(args);

        if (silent && !sender.hasPermission("hfp.unban.silent")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length != 1) { return false; }

        // проверяем активно ли кд
        if (sender instanceof Player && new User(sender.getName()).updateCooldown("unban")) {
            return true;
        }

        String user_nick = args[0];

        User user = new User(user_nick);

        // проверяем, что игрок забанен
        if (!user.isBanned()) {
            sender.sendMessage(MessagesData.getMSG_PlayerNotFound(user_nick));
            return true;
        }

        // удаляем из бд
        user.unban();

        // логируем
        Logging.INFO(MessagesData.getLogging_UnbanLog(sender.getName(), user_nick));

        // бродкастим
        if (!silent) {
            Bukkit.broadcast(MessagesData.getLogging_UnbanLog(sender.getName(), user_nick), "hfp.unban.notify");
        }

        return true;
    }

}
