package com.osx11.hypeflex.punishments.utils;

import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.exceptions.InvalidTimeIdentifier;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {

    public static String[] punishInfo(String input) throws InvalidTimeIdentifier {

        Pattern pattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        String[] output = new String[2]; // [0] = общее переведенное в секунды, [1] = текстовый формат

        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        int weeks = 0;
        int months = 0;
        int years = 0;
        boolean found = false;

        while(matcher.find()) {
            if (matcher.group() == null || matcher.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < matcher.groupCount(); i++) {
                if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }

            if (found) {
                if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {
                    years = Integer.parseInt(matcher.group(1));
                }
                if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
                    months = Integer.parseInt(matcher.group(2));
                }
                if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                    weeks = Integer.parseInt(matcher.group(3));
                }
                if (matcher.group(4) != null && !matcher.group(4).isEmpty()) {
                    days = Integer.parseInt(matcher.group(4));
                }
                if (matcher.group(5) != null && !matcher.group(5).isEmpty()) {
                    hours = Integer.parseInt(matcher.group(5));
                }
                if (matcher.group(6) != null && !matcher.group(6).isEmpty()) {
                    minutes = Integer.parseInt(matcher.group(6));
                }
                if (matcher.group(7) != null && !matcher.group(7).isEmpty()) {
                    seconds = Integer.parseInt(matcher.group(7));
                }
            }
        }

        if (!found) {
            throw new InvalidTimeIdentifier(input);
        }

        long totalTime = 0;

        String message = "";

        if (years != 0) {
            message = message + years + " " + MessagesData.getTimeIdentifier_Years() + " ";
            totalTime = totalTime + years * 31536000;
        }
        if (months != 0) {
            message = message + months + " " + MessagesData.getTimeIdentifier_Months() + " ";
            totalTime = totalTime + months * 2678400;
        }
        if (weeks != 0) {
            message = message + weeks + " " + MessagesData.getTimeIdentifier_Weeks() + " ";
            totalTime = totalTime + weeks * 604800;
        }
        if (days != 0) {
            message = message + days + " " + MessagesData.getTimeIdentifier_Days() + " ";
            totalTime = totalTime + days * 86400;
        }
        if (hours != 0) {
            message = message + hours + " " + MessagesData.getTimeIdentifier_Hours() + " ";
            totalTime = totalTime + hours * 3600;
        }
        if (minutes != 0) {
            message = message + minutes + " " + MessagesData.getTimeIdentifier_Minutes() + " ";
            totalTime = totalTime + minutes * 60;
        }
        if (seconds != 0) {
            message = message + seconds + " " + MessagesData.getTimeIdentifier_Seconds() + " ";
            totalTime = totalTime + seconds;
        }

        if (totalTime == 0) {
            throw new InvalidTimeIdentifier(input);
        }

        output[0] = Objects.toString(totalTime, null);
        output[1] = message.substring(0, message.length() - 1);

        return output;
    }

}
