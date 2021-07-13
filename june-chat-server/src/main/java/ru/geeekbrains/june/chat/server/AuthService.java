package ru.geeekbrains.june.chat.server;

public interface AuthService {

    void start();
    void stop();

    String getNickByLoginAndPass(String login, String password);

    void updateNickname (String nickname, String newNickname);
}
