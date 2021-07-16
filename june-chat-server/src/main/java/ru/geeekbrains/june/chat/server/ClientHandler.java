package ru.geeekbrains.june.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private String nickname;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> mainServerLogic()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mainServerLogic() {
        try {
            while (!consumeAuthorizeMessage(in.readUTF())) ;
            while (consumeRegularMessage(in.readUTF())) ;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Клиент " + getNickname() + " отключился");
            server.unsubscribe(this);
            closeConnection();
        }
    }

    private boolean consumeAuthorizeMessage(String message) {
        if (message.startsWith("/auth ")) {
            String[] tokens = message.split("\\s+");
            if (tokens.length < 3) {
                sendMessage("SERVER : Не указано имя пользователя или пароль.");
                return false;
            } else if (tokens.length > 3) {
                sendMessage("SERVER : Имя пользователя или пароль не может состоять из нескольких слов");
                return false;
            }
            nickname = server.getAuthService().getNickByLoginAndPass(tokens[1], tokens[2]);
            if (nickname == null) {
                System.out.println("SERVER : ERROR");
                return false;
            } else if (server.isNicknameUsed(nickname)) {
                sendMessage("SERVER : Данная учётная запись уже используется");
                return false;
            } else {
                sendMessage("/authok");
                sendMessage("/nickname " + nickname);
                sendMessage("Вы вошли в чат под именем: " + nickname);
                server.subscribe(this);
                return true;
            }
        } else {
            sendMessage("SERVER : Необходима авторизация");
            return false;
        }
    }

    private boolean consumeRegularMessage(String inputMessage) {
        if (inputMessage.startsWith("/")) {
            if (inputMessage.equals("/exit")) {
                sendMessage("/exit");
                return false;
            }
            if (inputMessage.startsWith("/w ")) {
                String[] tokens = inputMessage.split("\\s+", 3);
                server.sendPersonalMessage(this, tokens[1], tokens[2]);
            }
            return true;
        }
        server.broadcastMessage(nickname + ": " + inputMessage);
        return true;
    }

    private void closeConnection() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }
}
