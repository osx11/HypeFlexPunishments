package com.osx11.hypeflex.punishments;

import com.osx11.hypeflex.punishments.data.ConfigData;
import com.osx11.hypeflex.punishments.data.MessagesData;
import com.osx11.hypeflex.punishments.utils.Millis2Date;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MySQL {

    private static Connection connection;

    private static String login = ConfigData.getMySQL_login();
    private static String password = ConfigData.getMySQL_password();
    private static String url = ConfigData.getMySQL_url();

    private static Connection getConnection() throws SQLException {

        if (login != null)
            return DriverManager.getConnection(url, login, password);
        else return DriverManager.getConnection(url);
    }

    public static void insert(String query) {
        try {
            try {
                connection = getConnection();
            } catch (Exception e) {
                Logging.WARNING(MessagesData.getMSG_MySQL_ConnectionError());
                e.printStackTrace();
            }

            Statement statement = connection.createStatement();

            statement.executeUpdate(query);

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(String query, String columnLabel) {
        String result = null;
        try {
            try {
                connection = getConnection();
            } catch (Exception e) {
                Logging.WARNING(MessagesData.getMSG_MySQL_ConnectionError());
                e.printStackTrace();
            }

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                result = resultSet.getString(columnLabel);
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static long getLong(String query, String columnLabel) {
        long result = 0;
        try {
            try {
                connection = getConnection();
            } catch (Exception e) {
                Logging.WARNING(MessagesData.getMSG_MySQL_ConnectionError());
                e.printStackTrace();
            }

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                result = resultSet.getLong(columnLabel);
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int getInt(String query, String columnLabel) {
        int conut = 0;
        try {
            try {
                connection = getConnection();
            } catch (Exception e) {
                Logging.WARNING(MessagesData.getMSG_MySQL_ConnectionError());
                e.printStackTrace();
            }

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                conut = resultSet.getInt(columnLabel);
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conut;
    }

    public static boolean getBoolean(String query, String columnLabel) {
        boolean result = false;
        try {
            try {
                connection = getConnection();
            } catch (Exception e) {
                Logging.WARNING(MessagesData.getMSG_MySQL_ConnectionError());
                e.printStackTrace();
            }

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                if (resultSet.getBoolean(columnLabel)) {
                    result = true;
                }
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String[] getBanList() {
        ArrayList<String> issuedDate = new ArrayList<>();
        ArrayList<String> issuedTime = new ArrayList<>();
        ArrayList<String> nicks = new ArrayList<>();
        ArrayList<String> reasons = new ArrayList<>();
        try {
            try {
                connection = getConnection();
            } catch (Exception e) {
                Logging.WARNING(MessagesData.getMSG_MySQL_ConnectionError());
                e.printStackTrace();
            }

            Statement statement = connection.createStatement();

            ResultSet issuedDateRS = statement.executeQuery("SELECT issuedDate FROM bans");
            while (issuedDateRS.next()) {
                issuedDate.add(issuedDateRS.getString("issuedDate"));

            }

            ResultSet issuedTimeRS = statement.executeQuery("SELECT issuedTime FROM bans");
            while (issuedTimeRS.next()) {
                issuedTime.add(issuedTimeRS.getString("issuedTime"));

            }

            ResultSet nicksRS = statement.executeQuery("SELECT nick FROM bans");
            while (nicksRS.next()) {
                nicks.add(nicksRS.getString("nick"));

            }

            ResultSet reasonsRS = statement.executeQuery("SELECT reason FROM bans");
            while (reasonsRS.next()) {
                reasons.add(reasonsRS.getString("reason"));
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] result = new String[nicks.toArray().length];

        for (int i = 0; i < nicks.toArray().length; i++) {
            result[i] = "§7[" + issuedDate.get(i) + " " + issuedTime.get(i) + "] §f" + nicks.get(i) + " §7: §f" + reasons.get(i);
        }

        return result;
    }

    public static String[] getBanListIP() {
        ArrayList<String> issuedDate = new ArrayList<>();
        ArrayList<String> issuedTime = new ArrayList<>();
        ArrayList<String> IPs = new ArrayList<>();
        ArrayList<String> reasons = new ArrayList<>();
        try {
            try {
                connection = getConnection();
            } catch (Exception e) {
                Logging.WARNING(MessagesData.getMSG_MySQL_ConnectionError());
                e.printStackTrace();
            }

            Statement statement = connection.createStatement();

            ResultSet issuedDateRS = statement.executeQuery("SELECT issuedDate FROM bansIP");
            while (issuedDateRS.next()) {
                issuedDate.add(issuedDateRS.getString("issuedDate"));

            }

            ResultSet issuedTimeRS = statement.executeQuery("SELECT issuedTime FROM bansIP");
            while (issuedTimeRS.next()) {
                issuedTime.add(issuedTimeRS.getString("issuedTime"));

            }

            ResultSet IPsRS = statement.executeQuery("SELECT IP FROM bansIP");
            while (IPsRS.next()) {
                IPs.add(IPsRS.getString("IP"));

            }

            ResultSet reasonsRS = statement.executeQuery("SELECT reason FROM bansIP");
            while (reasonsRS.next()) {
                reasons.add(reasonsRS.getString("reason"));
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] result = new String[IPs.toArray().length];

        for (int i = 0; i < IPs.toArray().length; i++) {
            result[i] = "§7[" + issuedDate.get(i) + " " + issuedTime.get(i) + "] §f" + IPs.get(i) + " §7: §f" + reasons.get(i);
        }

        return result;
    }

    public static String[] getWarnlist(String nick) {
        ArrayList<String> issuedDate = new ArrayList<>();
        ArrayList<String> issuedTime = new ArrayList<>();
        ArrayList<String> warnsID = new ArrayList<>();
        ArrayList<String> reasons = new ArrayList<>();
        try {
            try {
                connection = getConnection();
            } catch (Exception e) {
                Logging.WARNING(MessagesData.getMSG_MySQL_ConnectionError());
                e.printStackTrace();
            }

            Statement statement = connection.createStatement();

            ResultSet issuedDateRS = statement.executeQuery("SELECT issuedDate FROM warns WHERE nick=\"" + nick + "\"");
            while (issuedDateRS.next()) {
                issuedDate.add(issuedDateRS.getString("issuedDate"));

            }

            ResultSet issuedTimeRS = statement.executeQuery("SELECT issuedTime FROM warns WHERE nick=\"" + nick + "\"");
            while (issuedTimeRS.next()) {
                issuedTime.add(issuedTimeRS.getString("issuedTime"));

            }

            ResultSet warnsIDRS = statement.executeQuery("SELECT warnID FROM warns WHERE nick=\"" + nick + "\"");
            while (warnsIDRS.next()) {
                warnsID.add(warnsIDRS.getString("warnID"));

            }

            ResultSet reasonsRS = statement.executeQuery("SELECT reason FROM warns WHERE nick=\"" + nick + "\"");
            while (reasonsRS.next()) {
                reasons.add(reasonsRS.getString("reason"));
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] result = new String[warnsID.toArray().length];

        for (int i = 0; i < warnsID.toArray().length; i++) {
            result[i] = "§7[" + issuedDate.get(i) + " " + issuedTime.get(i) + "] §fID " + warnsID.get(i) + " §7: §f" + reasons.get(i);
        }

        return result;
    }

    public static boolean stringIsExist(String table, String search, String value) {
        boolean exist = false;
        try {
            try {
                connection = getConnection();
            } catch (Exception e) {
                Logging.WARNING(MessagesData.getMSG_MySQL_ConnectionError());
                e.printStackTrace();
            }

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE " + search + "=\"" + value + "\"");
            if (resultSet.next()) {
                if (resultSet.getString(search) != null) {
                    exist = true;
                }
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return exist;
    }

}