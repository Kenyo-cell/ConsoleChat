package ru.netology.client;

import com.google.gson.annotations.SerializedName;

public class ClientSettings {
    @SerializedName("port")
    private int port;
    @SerializedName("host")
    private String host;

    public ClientSettings(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}
