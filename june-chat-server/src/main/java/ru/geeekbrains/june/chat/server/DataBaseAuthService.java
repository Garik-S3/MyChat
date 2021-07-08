package ru.geeekbrains.june.chat.server;

import java.sql.*;

public class DataBaseAuthService implements AuthService {

    private static Connection connection;
    private static Statement statement;

    @Override
    public void start(){
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:clientsdb.db");
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static String getNickByLoginAndPass(String login, String password) {
        String nickname = null;
        try (PreparedStatement ps = connection.prepareStatement(
                "select nickname from clients where login=? and password=?")) {
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet resSet = ps.executeQuery();
            if (resSet.next()) {
                 nickname = resSet.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nickname;
    }

//    public static void createClientsTable() throws SQLException {
//        String sql = "create table if not exists clients (\n" +
//                "id integer primary key autoincrement not null,\n" +
//                "login text not null,\n" +
//                "password text not null,\n" +
//                "nickname text not null\n" +
//                ");";
//        statement.executeUpdate(sql);
//    }
//
//    public static void insertClientsInTable() throws SQLException {
//        try (PreparedStatement preparedStatement =
//                     connection.prepareStatement("insert into clients (login, password, nickname) values (? ,? ,?)")) {
//            for (int i = 1; i < 6; i++) {
//                preparedStatement.setString(1, "login" + i);
//                preparedStatement.setString(2, "password" + i);
//                preparedStatement.setString(3, "nickname" + i);
//                preparedStatement.addBatch();
//            }
//            preparedStatement.executeBatch();
//        }
//    }
//
//    public static void dropTable() throws SQLException {
//        statement.execute("drop table if exists clients;");
//    }
}
