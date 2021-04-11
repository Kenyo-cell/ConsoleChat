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
    private String name = "user";
    private Socket socket;
    private ILogger logger;
    private ClientSettings settings;
    private final String SEPARATOR = ":";
    private final String END_MESSAGE = "*left chat*";

    public Client() {
        try (JsonReader reader = new JsonReader(new FileReader(new File(path)))) {
            settings = new Gson().fromJson(reader, ClientSettings.class);
            socket = new Socket(settings.getHost(), settings.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMsg() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while (!socket.isClosed()) {
                String msg = in.readLine();
                logger.log("Received message: %s".formatted(msg));
                System.out.println(msg);
            }
        } catch (IOException e) {
        }
    }

    public void run(){
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
