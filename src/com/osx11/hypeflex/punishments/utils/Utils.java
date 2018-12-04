package com.osx11.hypeflex.punishments.utils;

import com.osx11.hypeflex.punishments.exceptions.InvalidPunishReason;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /*
    Метод для получения полной причины работает следующим образом:
    args[] - все наши аргументы;
    метод берет все аргументы, начинающиеся с индекса index, и записывает их циклом в строковую переменную;
    если первый символ результата имеет числовой тип, то выдается исключение с сообщением об ошибке;
    возвращает результат в виде сложенных аргументов в одну строковую переменную
    */

    public static String GetFullReason(final String args[], final int index) throws InvalidPunishReason {
        String FullReason = "";
        for (int i = index; i < args.length; i++) {
            if (i == args.length - 1)
                FullReason = FullReason + args[i];
            else
                FullReason = FullReason + args[i] + " ";
        }
        Pattern pattern = Pattern.compile("^([0-9])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(FullReason);
        if (matcher.find()) {
            throw new InvalidPunishReason();
        }
        return FullReason;
    }

}
