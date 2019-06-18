package com.osx11.hypeflex.punishments;

import com.osx11.hypeflex.punishments.data.ConfigData;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.PlayerNotFoundInDB;
import com.osx11.hypeflex.punishments.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class User implements IUser {

    private Player player;
    private String nick;
    private String UUID;

    private String getUUIDfromdb() {
        return MySQL.getString("SELECT UUID FROM players WHERE nick=\"" + this.nick + "\"", "UUID");
    }

    public User(final Player player) {
        this.player = player;
        this.nick = player.getName();
        this.UUID = this.getUUIDfromdb();
    }
    public User(final String nick) {
        this.nick = nick;
        this.player = Bukkit.getPlayer(nick);
        this.UUID = this.getUUIDfromdb();
    }

    public boolean isOnline() {
        return this.player != null;
    }

    private void kickUser(final String reason) {
        this.player.kickPlayer(reason);
    }

    public void kickUser(final String reason, final boolean insert, final String issuer) {
        this.player.kickPlayer(reason);
        if (insert) {
            final String date = DateUtils.getCurrentDate();
            final String time = DateUtils.getCurrentTime();

            MySQL.insert("INSERT INTO kicks SET UUID=\"" + this.UUID + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\"");
        }
    }

    public String getIP() throws PlayerNotFoundInDB {
        String IP = null;
        if (MySQL.stringIsExist("players", "UUID", this.UUID)) {
            IP = MySQL.getString("SELECT IP FROM players WHERE UUID=\"" + this.UUID + "\"", "IP");
        }
        if (IP == null) {
            throw new PlayerNotFoundInDB(this.nick);
        }
        return IP;
    }

    public String getUUID() {
        return this.UUID;
    }

    /** проверяет, есть ли у игрока право на обход кулдауна
     *
     * Если есть:
     *   return false
     *
     * Если нет:
     *   проверяет, есть ли уже кд на команду у игрока
     *      Если нет:
     *         вписывает в таблицу UUID с командой и временем кулдауна на нее;
     *         return false;
     *      Если есть:
     *         складывает время кулдауна игрока из базы + время кулдауна по конфигу и сравнивает с текущим временем
     *            Если больше:
     *                 отправляет сообщение игроку о том, что он не может юзать команду;
     *                 return true;
     *            Если меньше:
     *                 обновляет строку в таблице с этой командой на новое время;
     *                 return false;
     *
     * @param command команда, кд на которую нужно установить
     * @return true if player has active cooldown; false if player doesn't have active cooldown
     */
    public boolean updateCooldown(final String command) {
        final String sql_GetUUID = "SELECT UUID FROM cooldowns WHERE UUID=\"" + this.UUID + "\" AND command=\"" + command + "\"";
        final String sql_GetCoolDown = "SELECT cooldown FROM cooldowns WHERE UUID=\"" + this.UUID + "\" AND command=\"" + command +"\"";
        final long cooldownConfig = ConfigData.getCooldown(command);

        if (!this.player.hasPermission("hfp." + command + ".cooldownBypass")) {
            if (MySQL.getString(sql_GetUUID, "UUID") != null) {
                if (MySQL.getLong(sql_GetCoolDown, "cooldown") + (cooldownConfig * 1000) > System.currentTimeMillis()) {
                    long cooldownTime = (MySQL.getLong(sql_GetCoolDown, "cooldown") + (cooldownConfig * 1000) - System.currentTimeMillis()) / 1000L;
                    this.player.sendMessage(MessagesData.getMSG_Cooldown(Objects.toString(cooldownTime, null)));
                    return true;
                } else
                    MySQL.insert("UPDATE cooldowns SET cooldown=\"" + System.currentTimeMillis() + "\" WHERE UUID=\"" + this.UUID + "\" AND command=\"" + command + "\"");
            } else
                MySQL.insert("INSERT INTO cooldowns SET UUID=\"" + this.UUID + "\", cooldown=\"" + System.currentTimeMillis() + "\", command=\"" + command +"\"");
        }

        return false;
    }

    public void sendMessage(final String message) {
        this.player.sendMessage(message);
    }

    public void ban(final String reason, final String issuer) {
        final String date = DateUtils.getCurrentDate();
        final String time = DateUtils.getCurrentTime();

        if (this.isBanned()) {
            MySQL.insert("UPDATE bans SET punishTimeString=\"*permanent*\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\" WHERE UUID=\"" + this.UUID + "\"");
        } else {
            MySQL.insert("INSERT INTO bans SET UUID=\"" + this.UUID + "\", punishTimeString=\"*permanent*\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\"");
        }
        if (this.isOnline()) {
            this.kickUser(MessagesData.getReason_BanReasonFormat(reason));
        }
    }

    public void ban(final String reason, final String issuer, final String punishTimeString, final long punishTimeSeconds, final String expire) {
        final String date = DateUtils.getCurrentDate();
        final String time = DateUtils.getCurrentTime();

        if (this.isBanned()) {
            MySQL.insert("UPDATE bans SET punishTimeString=\"" + punishTimeString + "\", punishTimeSeconds=\"" + punishTimeSeconds + "\", expire=\"" + (System.currentTimeMillis() + (punishTimeSeconds * 1000)) + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\" WHERE UUID=\"" + this.UUID + "\"");
        } else {
            MySQL.insert("INSERT INTO bans SET UUID=\"" + this.UUID + "\", punishTimeString=\"" + punishTimeString + "\", punishTimeSeconds=\"" + punishTimeSeconds + "\", expire=\"" + (System.currentTimeMillis() + (punishTimeSeconds * 1000)) + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\"");
        }
        if (this.isOnline()) {
            this.kickUser(MessagesData.getReason_TempbanReasonFormat(reason, punishTimeString, expire));
        }
    }

    public void mute(final String reason, final String issuer) {
        final String date = DateUtils.getCurrentDate();
        final String time = DateUtils.getCurrentTime();

        if (this.isMuted()) {
            MySQL.insert("UPDATE mutes SET punishTimeString=\"*permanent*\", punishTimeSeconds=0, reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\" WHERE UUID=\"" + this.UUID + "\"");
        } else {
            MySQL.insert("INSERT INTO mutes SET UUID=\"" + this.UUID + "\", punishTimeString=\"*permanent*\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\"");
        }
    }

    public void mute(final String reason, final String issuer, final String punishTimeString, final long punishTimeSeconds) {
        final String date = DateUtils.getCurrentDate();
        final String time = DateUtils.getCurrentTime();

        if (this.isMuted()) {
            MySQL.insert("UPDATE mutes SET punishTimeString=\"" + punishTimeString + "\", punishTimeSeconds=\"" + punishTimeSeconds + "\", expire=\"" + (System.currentTimeMillis() + (punishTimeSeconds * 1000)) + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\" WHERE UUID=\"" + this.UUID + "\"");
        } else {
            MySQL.insert("INSERT INTO mutes SET UUID=\"" + this.UUID + "\", punishTimeString=\"" + punishTimeString + "\", punishTimeSeconds=\"" + punishTimeSeconds + "\", expire=\"" + (System.currentTimeMillis() + (punishTimeSeconds * 1000)) + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\"");
        }
    }

    public void addWarn(final String reason, final String issuer) {
        final String date = DateUtils.getCurrentDate();
        final String time = DateUtils.getCurrentTime();

        MySQL.insert("INSERT INTO warns SET UUID=\"" + this.getUUID() + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\"");

    }

    public int getWarnsCount() { return MySQL.getInt("SELECT COUNT(UUID) FROM warns WHERE UUID=\"" + this.getUUID() + "\"", "COUNT(UUID)"); }

    public boolean hasExempt(final String command) {
        return this.hasPermission("hfp." + command + ".exempt");
    }

    public boolean hasPermission(final String perm) {
        if (this.isOnline()) {
            return player.hasPermission(perm);
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(java.util.UUID.fromString(this.UUID));
            return Main.perms.playerHas(null, offlinePlayer, perm);
        }
    }

    public boolean isBanned() { return MySQL.stringIsExist("bans", "UUID", this.UUID); }

    public boolean isMuted() { return MySQL.stringIsExist("mutes", "UUID", this.UUID); }

    public boolean hasActiveWarns() { return MySQL.stringIsExist("warns", "UUID", this.UUID); }

    public boolean hasWarn(String id) { return MySQL.getString("SELECT id FROM warns WHERE UUID=\"" + this.UUID + "\" AND id=\"" + id + "\"", "id") != null; }

    public void unban() { MySQL.insert("DELETE FROM bans WHERE UUID=\"" + this.UUID + "\""); }

    public void unmute() { MySQL.insert("DELETE FROM mutes WHERE UUID=\"" + this.UUID + "\""); }

    public void removeAllWarns() { MySQL.insert("DELETE FROM warns WHERE UUID=\"" + this.UUID + "\""); }

    public void removeWarn(String id) { MySQL.insert("DELETE FROM warns WHERE id=\"" + id + "\""); }

}

