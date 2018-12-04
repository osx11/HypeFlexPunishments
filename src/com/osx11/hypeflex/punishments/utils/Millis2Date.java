package com.osx11.hypeflex.punishments.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Millis2Date {

    public static String convertMillisToDate (final long millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm z", Locale.getDefault());
        Date date = new Date(millis);
        return simpleDateFormat.format(date);
    }

}
