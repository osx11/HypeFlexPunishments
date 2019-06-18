package com.osx11.hypeflex.punishments;

import com.osx11.hypeflex.punishments.commands.*;
import com.osx11.hypeflex.punishments.data.ConfigData;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.utils.Millis2Date;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class Main extends JavaPlugin implements Listener {

    static Permission perms = null;

    public void onEnable() {
        Logging.INFO("---------- LOADING HF Punishments ----------");

        boolean first = false;

        Logging.INFO(" ");

        File configFile = new File(getDataFolder() + File.separator + "config.yml");
        if (!configFile.exists()) {
            Logging.INFO("Creating new config file...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();

            first = true;
        }

        File messages = new File(getDataFolder() + File.separator + "messages.yml");
        if (!messages.exists()) {
            getLogger().info("Creating new messages file...");
            saveResource("messages.yml", true);
        }

        if (first) {
            Logging.INFO(" ");
            Logging.INFO("************ HypeFlex Punishments ************");
            Logging.INFO("*                                            *");
            Logging.INFO("*           THIS IS THE FIRST STARTUP.       *");
            Logging.INFO("*         PLEASE CONFIGURE PLUGIN IN         *");
            Logging.INFO("*   config.yml FILE AND RESTART THE SERVER.  *");
            Logging.INFO("*                                            *");
            Logging.INFO("*        THANKS FOR USING HFP (by osx11)     *");
            Logging.INFO("*                                            *");
            Logging.INFO("**********************************************");
            Logging.INFO(" ");

            this.setEnabled(false);
            return;

        }

        configData.setConfigData();
        messagesData.setMessagesData();

        setupPermissions();

        // создание таблиц
        final String sql_createtable_players = "CREATE TABLE IF NOT EXISTS `hfp`.`players` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `UUID` VARCHAR(36) NOT NULL , `nick` VARCHAR(255) NOT NULL , `IP` VARCHAR(15) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
        final String sql_createtable_bans = "CREATE TABLE IF NOT EXISTS `hfp`.`bans` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `UUID` VARCHAR(36) NOT NULL , `punishTimeString` VARCHAR(255) NOT NULL , `punishTimeSeconds` INT(11) NULL , `expire` INT(11) NULL , `reason` VARCHAR(255) NOT NULL , `issuedDate` DATE NOT NULL , `issuedTime` TIME NOT NULL , `issuedBy` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
        final String sql_createtable_bansip = "CREATE TABLE IF NOT EXISTS `hfp`.`bansIP` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `IP` VARCHAR(15) NOT NULL , `reason` VARCHAR(255) NOT NULL , `issuedDate` DATE NOT NULL , `issuedTime` TIME NOT NULL , `issuedBy` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
        final String sql_createtable_mutes = "CREATE TABLE IF NOT EXISTS `hfp`.`mutes` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `UUID` VARCHAR(36) NOT NULL , `punishTimeString` VARCHAR(255) NOT NULL , `punishTimeSeconds` INT(11) NULL , `expire` INT(11) NULL , `reason` VARCHAR(255) NOT NULL , `issuedDate` DATE NOT NULL , `issuedTime` TIME NOT NULL , `issuedBy` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
        final String sql_createtable_mutesip = "CREATE TABLE IF NOT EXISTS `hfp`.`mutesIP` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `IP` VARCHAR(15) NOT NULL , `reason` VARCHAR(255) NOT NULL , `issuedDate` DATE NOT NULL , `issuedTime` TIME NOT NULL , `issuedBy` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
        final String sql_createtable_kicks = "CREATE TABLE IF NOT EXISTS `hfp`.`kicks` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `UUID` VARCHAR(36) NOT NULL , `reason` VARCHAR(255) NOT NULL , `issuedDate` DATE NOT NULL , `issuedTime` TIME NOT NULL , `issuedBy` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
        final String sql_createtable_warns = "CREATE TABLE IF NOT EXISTS `hfp`.`warns` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `UUID` VARCHAR(36) NOT NULL , `reason` VARCHAR(255) NOT NULL , `issuedDate` DATE NOT NULL , `issuedTime` TIME NOT NULL , `issuedBy` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";

        MySQL.insert(sql_createtable_players);
        MySQL.insert(sql_createtable_bans);
        MySQL.insert(sql_createtable_bansip);
        MySQL.insert(sql_createtable_mutes);
        MySQL.insert(sql_createtable_mutesip);
        MySQL.insert(sql_createtable_kicks);
        MySQL.insert(sql_createtable_warns);

        // Регистрация команд
        getCommand("HFPunishments").setExecutor(new CommandReload(this));
        getCommand("kick").setExecutor(new CommandKick());
        getCommand("ban").setExecutor(new CommandBan());
        getCommand("banip").setExecutor(new CommandBanIP());
        getCommand("tempban").setExecutor(new CommandTempban());
        getCommand("unban").setExecutor(new CommandUnban());
        getCommand("unbanip").setExecutor(new CommandUnbanIP());
        getCommand("banlist").setExecutor(new CommandBanlist());
        getCommand("banlistip").setExecutor(new CommandBanlistIP());
        getCommand("mute").setExecutor(new CommandMute());
        getCommand("muteip").setExecutor(new CommandMuteIP());
        getCommand("tempmute").setExecutor(new CommandTempmute());
        getCommand("unmute").setExecutor(new CommandUnmute());
        getCommand("unmuteip").setExecutor(new CommandUnmuteIP());
        getCommand("warn").setExecutor(new CommandWarn());
        getCommand("warnlist").setExecutor(new CommandWarnlist());
        getCommand("unwarn").setExecutor(new CommandUnwarn());
        getCommand("pinfo").setExecutor(new CommandPinfo());

        this.getServer().getPluginManager().registerEvents(this, this);
        Logging.INFO("---------- HF Punishments LOADED SUCCESSFULLY ----------");
    }

    public void onDisable() { Logging.INFO("---------- HF Punishments SHUTTING DOWN ----------"); }


    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
    }

// ---------------------------------------------------------------------------------------------------------------------
    public final ConfigData configData = new ConfigData(this);
    public final MessagesData messagesData = new MessagesData(this);
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

        User user = new User(player);

        // -----------------------------------------------------------------------------------------------------------------
        // Проверяем, в бане ли игрок
        // по айпи

        if (new IPAddr(playerIP).isBanned()) {
            String reasonIP = MySQL.getString("SELECT reason FROM bansIP WHERE IP=\"" + playerIP + "\"", "reason");
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessagesData.getReason_BanIPReasonFormat(reasonIP));
            // информируем админов
            Bukkit.broadcast(MessagesData.getMSG_AttemptedToJoin(nick), "hfp.notifyDeniedJoin");
            return; // у бана по айпи самый высокий приоритет
        }

        // постоянный
        if (user.isBanned()) {
            final String reason = MySQL.getString("SELECT reason FROM bans WHERE UUID=\"" + UUID + "\"", "reason");
            final String punishTimeString = MySQL.getString("SELECT punishTimeString FROM bans WHERE UUID=\"" + UUID + "\"", "punishTimeString");

            if (punishTimeString.equals("*permanent*")) {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessagesData.getReason_BanReasonFormat(reason));
                // информируем админов
                Bukkit.broadcast(MessagesData.getMSG_AttemptedToJoin(nick), "hfp.notifyDeniedJoin");
            } else { // временный
                final long expire = MySQL.getLong("SELECT expire FROM bans WHERE UUID=\"" + UUID + "\"", "expire");
                if (expire > System.currentTimeMillis()) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessagesData.getReason_TempbanReasonFormat(reason, punishTimeString, Millis2Date.convertMillisToDate(expire)));
                    // информируем админов
                    Bukkit.broadcast(MessagesData.getMSG_AttemptedToJoin(nick), "hfp.notifyDeniedJoin");
                } else {
                    user.unban();
                }
            }
        }
        // -----------------------------------------------------------------------------------------------------------------
    }

    @EventHandler
    public void onChatEvent(final AsyncPlayerChatEvent event) {
        User user = new User(event.getPlayer());
        final String playerIP = event.getPlayer().getAddress().toString().replaceAll("/", "");
        final String UUID = user.getUUID();
    // -----------------------------------------------------------------------------------------------------------------
    // Проверяем, в муте ли игрок
        // по айпи
        if (new IPAddr(playerIP).isMuted()) {
            String reasonIP = MySQL.getString("SELECT reason FROM mutesIP WHERE IP=\"" + playerIP + "\"", "reason");
            event.setCancelled(true);
            user.sendMessage(MessagesData.getReason_MuteIPReasonFormat(reasonIP));
            return;
        }

        // перманент
        if (user.isMuted()) {
            final String reason = MySQL.getString("SELECT reason FROM mutes WHERE UUID=\"" + UUID + "\"", "reason");
            final String punishTimeString = MySQL.getString("SELECT punishTimeString FROM mutes WHERE UUID=\"" + UUID + "\"", "punishTimeString");

            if (punishTimeString.equals("*permanent*")) {
                event.setCancelled(true);
                user.sendMessage(MessagesData.getReason_MuteReasonFormat(reason));
            } else { // временный
                long expire = MySQL.getLong("SELECT expire FROM mutes WHERE UUID=\"" + UUID + "\"", "expire");
                if (expire > System.currentTimeMillis()) {
                    event.setCancelled(true);
                    expire = ((expire - System.currentTimeMillis()) / 1000L) / 60;
                    user.sendMessage(MessagesData.getReason_TempmuteReasonFormat(reason, punishTimeString, Objects.toString(expire, null)));
                } else {
                    MySQL.insert("DELETE FROM mutes WHERE UUID=\"" + UUID + "\"");
                }
            }
        }
    // -----------------------------------------------------------------------------------------------------------------
    }

}
