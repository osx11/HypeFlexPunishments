package com.osx11.hypeflex.punishments;

import com.osx11.hypeflex.punishments.exceptions.PlayerNotFoundInDB;

public interface IUser {

    boolean isOnline();

    void kickUser(final String reason, final boolean insert, final String issuer);

    String getIP() throws PlayerNotFoundInDB;

    String getUUID();

    boolean updateCooldown(final String command);

    void sendMessage(final String message);

    void ban(final String reason, final String issuer);

    void ban(final String reason, final String issuer, final String punishTimeString, final long punishTimeSeconds, final String expire);

    void mute(final String reason, final String issuer);

    void mute(final String reason, final String issuer, final String punishTimeString, final long punishTimeSeconds);

    void addWarn(final String reason, final String issuer);

    int getWarnsCount();

    boolean hasExempt(final String command);

    boolean hasPermission(final String perm);

    boolean isBanned();

    boolean isMuted();

    boolean hasActiveWarns();

    boolean hasWarn(String id);

    void unban();

    void unmute();

    void removeAllWarns();

    void removeWarn(String id);

}
