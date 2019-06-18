package com.osx11.hypeflex.punishments;

public interface IIPAddr {

    boolean isBanned();

    boolean isMuted();

    void ban(final String reason, final String issuer);

    void mute(final String reason, final String issuer);

    void unban();

    void unmute();

}
