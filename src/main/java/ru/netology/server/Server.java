package ru.netology.server;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import ru.netology.logger.FileLogger;
import ru.netology.logger.ILogger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class Server {
    private ConcurrentMap<String, List<AcceptedClientHandler>> chatRooms;
    private ExecutorService pool;
    private Date date;
    private ILogger logger;
    private static Server instance = null;
    private static ServerSettings settings = null;
    private static final String path = System.getProperty("user.dir") + "/ServerSettings.json";
    private static final String loggerPath = System.getProperty("user.dir") + "/serverLog.log";

    private Server() {
        pool = Executors.newCachedThreadPool();
        chatRooms = new ConcurrentHashMap<>();
        date = new Date();
        try (JsonReader reader = new JsonReader(new FileReader(new File(path)))) {
            settings = new Gson().fromJson(reader, ServerSettings.class);
            logger = new FileLogger(loggerPath);
        } catch (IOException e) { }
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void enterChat(String chatName, AcceptedClientHandler handler) {
        List<AcceptedClientHandler> handlers = chatRooms.get(chatName);
        if (handlers == null) {
            handlers = new CopyOnWriteArrayList<>();
        }

        handlers.add(handler);
        chatRooms.putIfAbsent(chatName, handlers);

        String msg = handler.getUserName() + " entered chat";
        notifySubs(new Message(chatName, msg));
        logger.log(msg);
    }

    public void removeFromChat(String chatName, AcceptedClientHandler handler) {
        chatRooms.remove(chatName, handler);
        logger.log(handler.getUserName() + " left chat");
    }

    public void notifySubs(Message msg) {
        logger.log(msg.getMsg());
        for (var handler : chatRooms.get(msg.getChatId())) {
            handler.sendMsg(String.format("[%s]: %s", date.toString(), msg.getMsg()));
        }
    }

    public void run() throws IOException {
        ServerSocket server = new ServerSocket(settings.getPort());
        logger.log("Server start");
        while (true) {
            try {
                Socket client = server.accept();
                pool.submit(new Thread(new AcceptedClientHandler(client)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static void main(String[] args) throws IOException {
        Server server = Server.getInstance();
        server.run();
    }
}
