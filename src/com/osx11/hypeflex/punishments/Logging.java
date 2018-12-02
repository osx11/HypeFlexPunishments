package com.osx11.hypeflex.punishments;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Logging {

    public static void INFO(String log) { Bukkit.getLogger().log(Level.INFO, log); }

    public static void WARNING(String log) {
        Bukkit.getLogger().log(Level.WARNING, log);
    }

}
