package ru.netology.server;

public class Message {
    private String chatId;
    private String msg;

    public Message(String chatId, String msg) {
        this.chatId = chatId;
        this.msg = msg;
    }

    public String getChatId() {
        return chatId;
    }

    public String getMsg() {
        return msg;
    }
}
