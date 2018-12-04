package com.osx11.hypeflex.punishments.commands;

import com.osx11.hypeflex.punishments.MySQL;
import com.osx11.hypeflex.punishments.data.MessagesData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CoolDown {

    public boolean hasCoolDown(final Player player, final String command, final int cooldownConfig) {
        final String UUID = player.getUniqueId().toString();
        final String sql_GetUUID = "SELECT UUID FROM cooldowns WHERE UUID=\"" + UUID + "\" AND command=\"" + command + "\"";
        final String sql_GetCoolDown = "SELECT cooldown FROM cooldowns WHERE UUID=\"" + UUID + "\" AND command=\"" + command +"\"";

        if (!player.hasPermission("hfp." + command + ".cooldownBypass")) {
            if (MySQL.getString(sql_GetUUID, "UUID") != null) {
                if (MySQL.getLong(sql_GetCoolDown, "cooldown") + (long)(cooldownConfig * 1000) > System.currentTimeMillis()) {
                    long cooldownTime = (MySQL.getLong(sql_GetCoolDown, "cooldown") + (long)(cooldownConfig * 1000) - System.currentTimeMillis()) / 1000L;
                    player.sendMessage(MessagesData.getMSG_Cooldown(Objects.toString(cooldownTime, null)));
                    return true;
                } else
                    MySQL.insert("UPDATE cooldowns SET cooldown=\"" + System.currentTimeMillis() + "\" WHERE UUID=\"" + UUID + "\" AND command=\"" + command + "\"");
            } else
                MySQL.insert("INSERT INTO cooldowns SET UUID=\"" + UUID + "\", cooldown=\"" + System.currentTimeMillis() + "\", command=\"" + command +"\"");
        }

        return false;
    }

}
