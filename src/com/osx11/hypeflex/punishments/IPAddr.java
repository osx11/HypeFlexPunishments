package com.osx11.hypeflex.punishments;

import com.osx11.hypeflex.punishments.utils.DateUtils;

public class IPAddr  implements IIPAddr{

    private String ip_addr;

    public IPAddr(String ip_addr) {
        this.ip_addr = ip_addr;
    }

    public boolean isBanned() {
        return MySQL.stringIsExist("bansIP", "IP", this.ip_addr);
    }

    public boolean isMuted() {
        return MySQL.stringIsExist("mutesIP", "IP", this.ip_addr);
    }

    public void ban(final String reason, final String issuer) {
        final String date = DateUtils.getCurrentDate();
        final String time = DateUtils.getCurrentTime();

        if (this.isBanned()) {
            MySQL.insert("UPDATE bansIP SET reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\" WHERE IP=\"" + this.ip_addr + "\"");
        } else {
            MySQL.insert("INSERT INTO bansIP SET IP=\"" + this.ip_addr + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\"");
        }
    }

    public void mute(final String reason, final String issuer) {
        final String date = DateUtils.getCurrentDate();
        final String time = DateUtils.getCurrentTime();

        if (this.isBanned()) {
            MySQL.insert("UPDATE mutesIP SET reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\" WHERE IP=\"" + this.ip_addr + "\"");
        } else {
            MySQL.insert("INSERT INTO mutesIP SET IP=\"" + this.ip_addr + "\", reason=\"" + reason + "\", issuedDate=\"" + date + "\", issuedTime=\"" + time + "\", issuedBy=\"" + issuer + "\"");
        }
    }

    public void unban() { MySQL.insert("DELETE FROM bansIP WHERE IP=\"" + this.ip_addr + "\""); }

    public void unmute() { MySQL.insert("DELETE FROM mutesIP WHERE IP=\"" + this.ip_addr + "\""); }

}
