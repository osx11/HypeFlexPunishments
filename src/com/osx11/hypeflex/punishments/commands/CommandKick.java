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

public class CommandKick implements CommandExecutor {

    private Main plugin;

    public CommandKick(Main plugin) {
        this.plugin = plugin;
    }

    private CoolDown coolDown = new CoolDown();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!User.hasPermission(sender.getName(), "hfp.kick")) {
            sender.sendMessage(MessagesData.getMSG_InsufficientPermissions());
            return true;
        }

        if (args.length < 1) { return false; }

        GregorianCalendar gregorianCalendar = new GregorianCalendar();

        String punishableNick = args[0];
        String reason = MessagesData.getReason_DefaultReason();
        Player player = Bukkit.getPlayer(punishableNick);
        int cooldownConfig = ConfigData.getCoolDownKick();
        String date = String.valueOf(gregorianCalendar.get(Calendar.YEAR)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.MONTH)) + "-" + String.valueOf(gregorianCalendar.get(Calendar.DATE));
        String time = String.valueOf(gregorianCalendar.get(Calendar.HOUR)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.MINUTE)) + ":" + String.valueOf(gregorianCalendar.get(Calendar.SECOND));

        // если игрок оффлайн
        if (!User.isOnline(player)) {
            sender.sendMessage(MessagesData.getMSG_PlayerNotFound(punishableNick));
            return true;
        }

        // если у чела иммунитет
        if (User.hasPermission(punishableNick, "hfp.kick.exempt") && sender instanceof Player) {
            sender.sendMessage(MessagesData.getMSG_Exempt(punishableNick));
            return true;
        }

        // проверяем на активное кд
        if (sender instanceof Player) {
            if (coolDown.hasCoolDown(Bukkit.getPlayer(sender.getName()), punishableNick, "kick", cooldownConfig))
                return true;
        }

        // пишем причину
        try {
            if (args.length > 1) {
                reason = Utils.GetFullReason(args, 1);
            }
        } catch (InvalidPunishReason e) {
            sender.sendMessage(e.getMessage());
            return true;
        }

        // кикаем
        User.kickUser("kick", player, reason);

        // добавляем в бд
        MySQL.insert("INSERT INTO kicks SET nick=\"" + punishableNick + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + sender.getName() + "\"");

        // логируем
        Logging.INFO(MessagesData.getLogging_KickLog(sender.getName(), punishableNick, reason));

        // бродкастим
        Bukkit.broadcastMessage(MessagesData.getLogging_KickLog(sender.getName(), punishableNick, reason));

        return true;
    }

}
