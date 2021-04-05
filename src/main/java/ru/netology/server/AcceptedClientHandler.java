package ru.netology.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class AcceptedClientHandler implements Runnable {
    private Socket client;
    private final Server server;

    public String getUserName() {
        return userName;
    }

    private String userName;
    private String userChatName;
    private PrintWriter out;
    private final String SEPARATOR = ":";

    public AcceptedClientHandler(Socket client) {
        this.client = client;
        server = Server.getInstance();
    }

    public void sendMsg(String msg) {
        out.println(msg);
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            out = new PrintWriter(client.getOutputStream(), true);
            String[] userInfo = in.readLine().trim().split(SEPARATOR);
            userName = userInfo[0];
            userChatName = userInfo[1];

            server.enterChat(userChatName, this);

            while (!client.isClosed()) {

                String msg = "";
                try {
                   msg = in.readLine();
                } catch (SocketException e) {
                    client.close();
                    continue;
                }

                server.notifySubs(new Message(userChatName, formatMessage(msg)));
            }

            server.removeFromChat(userChatName, this);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    private String formatMessage(String msg) {
        return String.format("%s: %s", userName, msg);
    }
}
