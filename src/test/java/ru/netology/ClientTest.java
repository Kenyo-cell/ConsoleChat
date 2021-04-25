package ru.netology;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.netology.client.Client;
import ru.netology.client.ClientSettings;
import ru.netology.logger.FileLogger;
import ru.netology.logger.ILogger;

import java.io.*;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ClientTest {
    private String name = "test";
    private String chatName = "testChat";
    private String testMsg = "Test msg";
    private final String path = System.getProperty("user.dir") + "/ClientSettings.json";
    private final String loggerPath = System.getProperty("user.dir") + "/test.log";

    private class TestClient extends Client {


        private Socket socket;
        private ILogger logger;
        private ClientSettings settings;
        private final String SEPARATOR = ":";

        public TestClient() {
            try (JsonReader reader = new JsonReader(new FileReader(new File(path)))) {
                settings = new Gson().fromJson(reader, ClientSettings.class);
                socket = new Socket(settings.getHost(), settings.getPort());
                logger = new FileLogger(loggerPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                logger.log("Client chose name: %s".formatted(name));
                logger.log("%s enters chat room: %s".formatted(name, chatName));

                String info = name + SEPARATOR + chatName;
                out.println(info);


                logger.log("Sending message %s".formatted(testMsg));
                out.println(testMsg);

                logger.log("%s end".formatted(name));
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> l = List.of(
            "Client chose name: test",
            "test enters chat room: testChat",
            "Sending message Test msg",
            "test end"
    );

    @Test
    public void logFileExistsTest() {
        new TestClient().run();

        Assertions.assertTrue(new File(loggerPath).exists());
    }

    @Test
    public void correctWriteLogTest() throws IOException {
        File f = new File(loggerPath);
        InputStreamReader in = new InputStreamReader(new FileInputStream(f));
        CharBuffer buff = CharBuffer.wrap(new char[(int) f.length()]);
        in.read(buff);
        List<String> s = Arrays.asList(String.valueOf(buff.array()).split("\n"));
        s = s.subList(s.size() - 4, s.size());
        s = s.stream().map(el -> el.substring(32)).collect(Collectors.toList());
        Assertions.assertTrue(s.containsAll(l));
    }
}
