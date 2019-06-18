package com.osx11.hypeflex.punishments.utils;

import com.osx11.hypeflex.punishments.exceptions.InvalidPunishReason;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /**
     * Метод работает следующим образом:
     * args[] - все наши аргументы;
     * метод берет все аргументы, начинающиеся с индекса index, и записывает их циклом в строковую переменную;
     * если первый символ результата имеет числовой тип, то выдается исключение с сообщением об ошибке;
     * возвращает результат в виде сложенных аргументов в одну строку
     *
     * @param args String array of arguments
     * @param index индекс, начиная с которого будут складываться аргументы
     * @throws InvalidPunishReason если первый символ результата - число
     * @return все аргументы, начиная с индекса index, сложенные в одну строку
     */

    public static String GetFullReason(final String[] args, final int index) throws InvalidPunishReason {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = index; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }

        String output = stringBuilder.toString();
        output = output.trim();

        Pattern pattern = Pattern.compile("^([0-9])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            throw new InvalidPunishReason();
        }

        return output;
    }

    /**
     * Удаляет все элементы из массива, начинающиеся с '-'.
     *
     * @param array String array, в котором нужно удалить элементы
     * @return String array без элементов, начинающихся с '-'
     */
    public static String[] removeFlags(String[] array) {
        ArrayList<String> arraylist = new ArrayList<>(Arrays.asList(array));

        Pattern pattern = Pattern.compile("-\\w+");

        for (int i = 0; i < arraylist.size(); i++) {
            while (pattern.matcher(arraylist.get(i)).find()) {
                arraylist.remove(arraylist.get(i));
            }
        }

        String[] output = new String[arraylist.size()];
        arraylist.toArray(output);

        return output;
    }

    public static boolean flagSilent(String[] array) { return Arrays.asList(array).contains("-s"); }
    public static boolean flagForce(String[] array) { return Arrays.asList(array).contains("-f"); }
    public static boolean flagAll(String[] array)  { return Arrays.asList(array).contains("-a"); }

    /**
     * Проверяет строку на соответствие с паттерном IP-адреса.
     *
     * @param string строка, которую нужно проверить
     * @return true, если строка соответствует паттерну
     */
    public static boolean isiP(String string) {
        Pattern IPPattern = Pattern.compile("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))");
        Matcher matcher = IPPattern.matcher(string);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                return true;
            }
        }
        return false;
    }

}
