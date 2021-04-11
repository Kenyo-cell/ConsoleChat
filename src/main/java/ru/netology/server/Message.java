package ru.netology.server;

public class Message {
    private String chatId;
    private String msg;

    public String getAuthor() {
        return author;
    }

    private String author;

    public Message(String chatId, String author, String msg) {
        this.chatId = chatId;
        this.msg = msg;
        this.author = author;
    }

    public String getChatId() {
        return chatId;
    }

    public String getMsg() {
        return msg;
    }
}
