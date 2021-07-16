package ru.geekbrains.june.chat.client;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class ChatHistory {

    public static List<String> loadClientHistory(String nickname, int maxLines) throws IOException {
        File directory = new File("chat_history");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File("chat_history/history_" + nickname + ".txt");
        if (!file.exists()) {
            file.createNewFile();
        }

        List<String> list = new LinkedList<>();
        String line;
        try { BufferedReader buffReader =
                new BufferedReader(new FileReader("chat_history/history_" + nickname + ".txt"));
            while ((line = buffReader.readLine()) != null) {
                list.add(line);
                if (list.size() > maxLines) {
                    list.remove(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveClientHistory(String nickname, String message) {
        try { BufferedWriter buffWriter =
                    new BufferedWriter(new FileWriter("chat_history/history_" + nickname + ".txt", true));
            buffWriter.write(message + "\n");
            buffWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
