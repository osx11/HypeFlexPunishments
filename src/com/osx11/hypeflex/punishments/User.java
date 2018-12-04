package com.osx11.hypeflex.punishments;

import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.PlayerNotFoundInDB;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class User {

    public static boolean isOnline(final Player player) {
        if (player != null) {
            return true;
        }
        return false;
    }

    public static void kickUser(final String reasonFormat, final Player player, final String reason) {
        if (reasonFormat.equals("kick")) {
            player.kickPlayer(MessagesData.getReason_KickReasonFormat(reason));
        }
        if (reasonFormat.equals("ban")) {
            player.kickPlayer(MessagesData.getReason_BanReasonFormat(reason));
        }
        if (reasonFormat.equals("banip")) {
            player.kickPlayer(MessagesData.getReason_BanIPReasonFormat(reason));
        }
    }

    public static void kickUser(final String reasonFormat, final Player player, final String reason, final String punishTimeString, final String expire) {
        if (reasonFormat.equals("kick")) {
            player.kickPlayer(MessagesData.getReason_KickReasonFormat(reason));
        }
        if (reasonFormat.equals("ban")) {
            player.kickPlayer(MessagesData.getReason_BanReasonFormat(reason));
        }
        if (reasonFormat.equals("banip")) {
            player.kickPlayer(MessagesData.getReason_BanIPReasonFormat(reason));
        }
        if (reasonFormat.equals("tempban")) {
            player.kickPlayer(MessagesData.getReason_TempbanReasonFormat(reason, punishTimeString, expire));
        }
    }

    public static String getIP(final String nick) throws PlayerNotFoundInDB {
        String IP;
        if (MySQL.stringIsExist("players", "nick", nick)) {
            IP = MySQL.getString("SELECT IP FROM players WHERE nick=\"" + nick + "\"", "IP");
        } else {
            throw new PlayerNotFoundInDB(nick);
        }
        return IP;
    }

    public static boolean hasPermission(final String nick, final String permission) {
        final Player player = Bukkit.getPlayer(nick);
        if (!User.isOnline(player)) {
            PermissionUser permissionUser = PermissionsEx.getUser(nick);
            return permissionUser.has(permission);
        } else {
            return player.hasPermission(permission);
        }
    }

    public static boolean hasPermission(final Player player, final String permission) {
        if (!User.isOnline(player)) {
            PermissionUser permissionUser = PermissionsEx.getUser(player);
            return permissionUser.has(permission);
        } else {
            return player.hasPermission(permission);
        }
    }

    public static boolean hasPermission(final CommandSender sender, final String permission) {
        if (sender instanceof Player) {
            return User.hasPermission(sender.getName(), permission);
        } else {
            return true;
        }
    }

    public static boolean isBanned(final String nick) {
        return MySQL.stringIsExist("bans", "nick", nick);
    }

    public static boolean isBannedIP(final String playerIP) {
        return MySQL.stringIsExist("bansIP", "IP", playerIP);
    }

    public static boolean isMuted(final String nick) {
        return MySQL.stringIsExist("mutes", "nick", nick);
    }

    public static boolean isMutedIP(final String playerIP) {
        return MySQL.stringIsExist("mutesIP", "IP", playerIP);
    }

}

