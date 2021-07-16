package ru.geeekbrains.june.chat.server;

import java.sql.*;

public class DatabaseAuthService implements AuthService {

    private static Connection connection;
    private static Statement statement;

    @Override
    public void start() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:clientsdb.db");
            statement = connection.createStatement();
            createClientsTable();
            insertClientsInTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void createClientsTable() throws SQLException {
        String sql = "create table if not exists clients (\n" +
                "id integer primary key autoincrement not null,\n" +
                "login text not null,\n" +
                "password text not null,\n" +
                "nickname text not null\n" +
                ");";
        statement.executeUpdate(sql);
    }

    public void insertClientsInTable() {
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement("insert into clients (login, password, nickname) values (? ,? ,?)")) {
            for (int i = 1; i < 6; i++) {
                preparedStatement.setString(1, "login" + i);
                preparedStatement.setString(2, "password" + i);
                preparedStatement.setString(3, "nickname" + i);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (statement != null) {
            try {
                statement.close();
                dropTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void dropTable() throws SQLException {
        statement.execute("drop table clients;");
    }

    @Override
    public String getNickByLoginAndPass(String login, String password) {
        try (PreparedStatement ps = connection.prepareStatement(
                "select nickname from clients where login = ? and password = ?")) {
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet resSet = ps.executeQuery();
            if (resSet.next()) {
                return resSet.getString("nickname");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateNickname(String nickname, String newNickname) {
        try (PreparedStatement ps = connection.prepareStatement(
                "update clients set nickname = ? where nickname = ?")) {
            ps.setString(1, newNickname);
            ps.setString(2, nickname);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
