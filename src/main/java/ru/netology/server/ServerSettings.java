package ru.netology.server;

import com.google.gson.annotations.SerializedName;

public class ServerSettings {
    @SerializedName("port") private int port;

    public ServerSettings(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
