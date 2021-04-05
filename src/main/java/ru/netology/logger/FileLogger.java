package ru.netology.logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class FileLogger implements ILogger, Closeable {
    private FileWriter writer;
    private Date date;
    private final String FORMAT_LOGGER_MESSAGE = "[%s]: %s\n";

    public FileLogger(String path) throws IOException {
        if (!(new File(path).exists())) new File(path).createNewFile();

        writer = new FileWriter(path, true);
        date = new Date();
    }

    @Override
    public void log(String msg) {
        try {
            writer.write(String.format(FORMAT_LOGGER_MESSAGE, date.toString(), msg));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
