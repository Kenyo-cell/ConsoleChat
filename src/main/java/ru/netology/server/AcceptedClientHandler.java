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
            String info = in.readLine();
            userName = info.split(SEPARATOR)[0];
            userChatName = info.split(SEPARATOR)[1];

            server.enterChat(userChatName, this);

            while (!client.isClosed()) {

                String msg = "";
                try {
                    msg = in.readLine();
                    if (msg == null) throw new SocketException();
                } catch (SocketException e) {
                    client.close();
                    continue;
                }

                server.notifySubs(new Message(userChatName, userName, formatMessage(msg)));
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
