package ru.netology.client;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import ru.netology.logger.FileLogger;
import ru.netology.logger.ILogger;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String path = System.getProperty("user.dir") + "/ClientSettings.json";
    private static final String loggerPath = System.getProperty("user.dir") + "/";
    private String name;
    private Socket socket;
    private ILogger logger;
    private ClientSettings settings;
    private final String SEPARATOR = ":";
    private final String END_MESSAGE = "*left chat*";

    public Client() throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(new File(path)))) {
            settings = new Gson().fromJson(reader, ClientSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = new Socket(settings.getHost(), settings.getPort());
    }

    public void getMsg() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while (!socket.isClosed()) {
                String msg = in.readLine();
                logger.log("Received message: %s".formatted(msg));
                System.out.println(msg);
            }
        } catch (IOException e) { }
    }

    private void run() throws IOException {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Enter user name");
            name = scanner.nextLine();
            logger = new FileLogger(loggerPath + name + ".log");
            logger.log("Client chose name: %s".formatted(name));

            System.out.println("Enter chat name u want enter");
            String chatName = scanner.nextLine();
            logger.log("%s enters chat room: %s".formatted(name, chatName));

            String info = name + SEPARATOR + chatName;
            out.println(info);

            Thread t = new Thread(this::getMsg);
            t.setDaemon(true);
            t.start();

            while (!socket.isClosed()) {
                String msg = scanner.nextLine();
                if (msg.equalsIgnoreCase("/exit")) {
                    out.println(END_MESSAGE);
                    socket.close();
                    continue;
                }

                logger.log("Sending message %s".formatted(msg));
                out.println(msg);
            }
            logger.log("%s end".formatted(name));
        }
    }

    public static void main(String[] args){
        try {
            Client client = new Client();
            client.run();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
