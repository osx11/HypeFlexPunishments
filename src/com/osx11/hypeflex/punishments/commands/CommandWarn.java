package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.Logging;
import com.osx11.hypeflex.punishments.Main;
import com.osx11.hypeflex.punishments.MySQL;
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

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CommandWarn implements CommandExecutor {

    private Main plugin;

    public CommandWarn(Main plugin) {
        this.plugin = plugin;
    }

    private CoolDown coolDown = new CoolDown();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender, "hfp.warn")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        boolean forceSpecified = false;
        if (args[0].equalsIgnoreCase("-f")) {
            if (User.hasPermission(sender, "hfp.warn.force")) {
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

        GregorianCalendar gregorianCalendar = new GregorianCalendar();

        String reason = MessagesData.getReason_DefaultReason();
        final Player player = Bukkit.getPlayer(punishableNick);
        final String UUID = MySQL.getString("SELECT UUID FROM players WHERE nick=\"" + punishableNick + "\"", "UUID");
        final int cooldownConfig = ConfigData.getCoolDownWarn();
        final String date = String.valueOf(gregorianCalendar.get(Calendar.YEAR)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.MONTH)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.DATE));
        final String time = String.valueOf(gregorianCalendar.get(Calendar.HOUR)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.MINUTE)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.SECOND));

        // если игрок не найден
        if (!User.isOnline(player)) {
            if (!User.hasPermission(sender, "hfp.warn.offline")) {
                sender.sendMessage(MessagesData.getMSG_PlayerIsOffline());
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
        if (User.hasPermission(punishableNick, "hfp.warn.exempt") && sender instanceof Player) {
            sender.sendMessage(MessagesData.getMSG_Exempt(punishableNick));
            return true;
        }

        // проверяем активно ли кд
        if (sender instanceof Player) {
            if (coolDown.hasCoolDown(Bukkit.getPlayer(sender.getName()), "warn", cooldownConfig))
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
            player.sendMessage(MessagesData.getReason_WarnReasonFormat(reason));
        }

        int warnsCount = MySQL.getInt("SELECT COUNT(nick) FROM warns WHERE nick=\"" + punishableNick + "\"", "COUNT(nick)") + 1;
        int warnID = MySQL.getInt("SELECT max(warnID) as warnID from warns WHERE nick=\"" + punishableNick + "\"", "warnID") + 1;

        // добавляем в бд
        MySQL.insert("INSERT INTO warns SET nick=\"" + punishableNick + "\", reason=\"" + reason + "\", warnID=\"" + String.valueOf(warnID) +"\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + sender.getName() + "\"");


        // логируем
        Logging.INFO(MessagesData.getLogging_WarnLog(sender.getName(), punishableNick, reason));

        // бродкастим
        Bukkit.broadcast(MessagesData.getLogging_WarnLog(sender.getName(), punishableNick, reason), "hfp.warn.notify");

        //--------------------------------------------------------------------------------------------------------------
        // выполнение автоматических команд
        String warnsCountCommand[] = ConfigData.getWarnsCountCommands();
        boolean isLastWarn = false;

        if (ConfigData.getWarns_AutoExecute()) {
            for (int i = 0; i < warnsCountCommand.length; i++) {
                if (warnsCount == Integer.parseInt(warnsCountCommand[i])) {
                    if (warnsCount == Integer.parseInt(warnsCountCommand[warnsCountCommand.length - 1])) {
                        isLastWarn = true;
                    }

                    boolean ifOnlineOnly = plugin.getConfig().getBoolean("warns.on_warn_count." + warnsCountCommand[i] + ".if_player_is_online"); // если в конфиге стоит исполнение команды только если игрок онлайн
                    String executeCommand = plugin.getConfig().getString("warns.on_warn_count." + warnsCountCommand[i] + ".command").replaceAll("%player%", punishableNick).replaceAll("%warnsCount%", warnsCountCommand[i]);

                    if (!User.isOnline(player)) {
                        if (!ifOnlineOnly) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executeCommand);
                        }
                    } else {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executeCommand);
                    }
                }
            }
            if (ConfigData.getWarns_DeleteAfterLast()) {
                if (isLastWarn) {
                    MySQL.insert("DELETE FROM warns WHERE nick=\"" + punishableNick + "\"");
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        return true;
    }

}
