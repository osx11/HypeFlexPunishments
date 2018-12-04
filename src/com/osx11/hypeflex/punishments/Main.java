package com.osx11.hypeflex.punishments;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.osx11.hypeflex.punishments.commands.*;
import com.osx11.hypeflex.punishments.data.ConfigData;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.PlayerNotFoundInDB;
import com.osx11.hypeflex.punishments.utils.Millis2Date;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class Main extends JavaPlugin implements Listener {

    public void onEnable() {

        File configFile = new File(getDataFolder() + File.separator + "config.yml");
        if (!configFile.exists()) {
            getLogger().info("Creating new config file...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }

        File messages = new File(getDataFolder() + File.separator + "messages.yml");
        if (!messages.exists()) {
            getLogger().info("Creating new messages file...");
            saveResource("messages.yml", true);
        }

        configData.setConfigData();
        messagesData.setMessagesData();

        // Регистрация команд
        getCommand("HFPunishments").setExecutor(new CommandReload(this));
        getCommand("kick").setExecutor(new CommandKick(this));
        getCommand("ban").setExecutor(new CommandBan(this));
        getCommand("banip").setExecutor(new CommandBanIP(this));
        getCommand("tempban").setExecutor(new CommandTempban(this));
        getCommand("unban").setExecutor(new CommandUnban(this));
        getCommand("unbanip").setExecutor(new CommandUnbanIP(this));
        getCommand("banlist").setExecutor(new CommandBanlist(this));
        getCommand("banlistip").setExecutor(new CommandBanlistIP(this));
        getCommand("mute").setExecutor(new CommandMute(this));
        getCommand("muteip").setExecutor(new CommandMuteIP(this));
        getCommand("tempmute").setExecutor(new CommandTempmute(this));
        getCommand("unmute").setExecutor(new CommandUnmute(this));
        getCommand("unmuteip").setExecutor(new CommandUnmuteIP(this));
        getCommand("warn").setExecutor(new CommandWarn(this));
        getCommand("warnlist").setExecutor(new CommandWarnlist(this));
        getCommand("unwarn").setExecutor(new CommandUnwarn(this));

        this.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("---------- HF Punishments LOADED SUCCESSFULLY ----------");
    }

    public void onDisable() { getLogger().info("---------- HF Punishments SHUTTING DOWN ----------"); }

// ---------------------------------------------------------------------------------------------------------------------
    private final ConfigData configData = new ConfigData(this);
    private final MessagesData messagesData = new MessagesData(this);
// ---------------------------------------------------------------------------------------------------------------------

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        // -----------------------------------------------------------------------------------------------------------------
        // Записываем UUID в базу, если не сущесвует
        final String nick = event.getPlayer().getName();
        final Player player = event.getPlayer();
        final String UUID = player.getUniqueId().toString();
        final String playerIP = event.getAddress().toString().replaceAll("/", "");
        if (!MySQL.stringIsExist("players", "nick", nick)) {
            MySQL.insert("INSERT INTO players SET IP=\"" + playerIP + "\", nick=\"" + nick + "\", UUID=\"" + UUID + "\"");
        }
        // -----------------------------------------------------------------------------------------------------------------

        final String reason = MySQL.getString("SELECT reason FROM bans WHERE nick=\"" + nick + "\"", "reason");
        final String punishTimeString = MySQL.getString("SELECT punishTimeString FROM bans WHERE nick=\"" + nick + "\"", "punishTimeString");

        // -----------------------------------------------------------------------------------------------------------------
        // Проверяем, в бане ли игрок
        // по айпи
        if (User.isBannedIP(playerIP)) {
            String reasonIP = MySQL.getString("SELECT reason FROM bansIP WHERE IP=\"" + playerIP + "\"", "reason");
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessagesData.getReason_BanIPReasonFormat(reasonIP));
            // информируем админов
            for (final Player p : Bukkit.getOnlinePlayers()) {
                if (User.hasPermission(p, "hfp.notifyDeniedJoin")) {
                    p.sendMessage(MessagesData.getMSG_AttemptedToJoin(nick));
                }
            }
            return; // у бана по айпи самый высокий приоритет
        }

        // постоянный
        if (User.isBanned(nick)) {
            if (punishTimeString.equals("*permanent*")) {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessagesData.getReason_BanReasonFormat(reason));
                // информируем админов
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    if (User.hasPermission(p, "hfp.notifyDeniedJoin")) {
                        p.sendMessage(MessagesData.getMSG_AttemptedToJoin(nick));
                    }
                }
            }
        }

        // временный
        if (User.isBanned(nick)) {
            if (!punishTimeString.equals("*permanent*")) {
                final long expire = MySQL.getLong("SELECT expire FROM bans WHERE nick=\"" + nick + "\"", "expire");
                if (expire > System.currentTimeMillis()) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessagesData.getReason_TempbanReasonFormat(reason, punishTimeString, Millis2Date.convertMillisToDate(expire)));
                    // информируем админов
                    for (final Player p : Bukkit.getOnlinePlayers()) {
                        if (User.hasPermission(p, "hfp.notifyDeniedJoin")) {
                            p.sendMessage(MessagesData.getMSG_AttemptedToJoin(nick));
                        }
                    }
                } else {
                    MySQL.insert("DELETE FROM bans WHERE nick=\"" + nick + "\"");
                }
            }
        }
        // -----------------------------------------------------------------------------------------------------------------
    }

    @EventHandler
    public void onChatEvent(final AsyncPlayerChatEvent event) {
        final String nick = event.getPlayer().getName();
        final Player player = event.getPlayer();
        String playerIP;
        try {
            playerIP = User.getIP(nick);
        } catch (PlayerNotFoundInDB e) {
            Logging.WARNING(e.getMessage());
            User.kickUser("kick", player, MessagesData.getMSG_Error());
            return;
        }
        final String reason = MySQL.getString("SELECT reason FROM mutes WHERE nick=\"" + nick + "\"", "reason");
        final String punishTimeString = MySQL.getString("SELECT punishTimeString FROM mutes WHERE nick=\"" + nick + "\"", "punishTimeString");
    // -----------------------------------------------------------------------------------------------------------------
    // Проверяем, в муте ли игрок
        // по айпи
        if (User.isMutedIP(playerIP)) {
            String reasonIP = MySQL.getString("SELECT reason FROM mutesIP WHERE IP=\"" + playerIP + "\"", "reason");
            event.setCancelled(true);
            player.sendMessage(MessagesData.getReason_MuteIPReasonFormat(reasonIP));
        }

        // временный
        if (User.isMuted(nick)) {
            if (!punishTimeString.equals("*permanent*")) {
                long expire = MySQL.getLong("SELECT expire FROM mutes WHERE nick=\"" + nick + "\"", "expire");
                if (expire > System.currentTimeMillis()) {
                    event.setCancelled(true);
                    expire = ((expire - System.currentTimeMillis()) / 1000L) / 60;
                    player.sendMessage(MessagesData.getReason_TempmuteReasonFormat(reason, punishTimeString, Objects.toString(expire, null)));
                } else {
                    MySQL.insert("DELETE FROM mutes WHERE nick=\"" + nick + "\"");
                }
            }
        }

        // перманент
        if (User.isMuted(nick)) {
            if (punishTimeString.equals("*permanent*")) {
                event.setCancelled(true);
                player.sendMessage(MessagesData.getReason_MuteReasonFormat(reason));
            }
        }
    // -----------------------------------------------------------------------------------------------------------------
    }

}
