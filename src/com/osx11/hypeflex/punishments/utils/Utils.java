package com.osx11.hypeflex.punishments.utils;

public class Utils {

    public static String GetFullReason(String args[], int index) {
        String FullReason = "";
        for (int i = index; i < args.length; i++) {
            if (i == args.length - 1)
                FullReason = FullReason + args[i];
            else
                FullReason = FullReason + args[i] + " ";
        }
        return FullReason;
    }

}
