package com.example.websocketdemo.model;


import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String time;

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    private List<String> userList = new ArrayList<>();

    public String getTypeOn() {
        return typeOn;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTypeOn(String typeOn) {
        this.typeOn = typeOn;
    }

    private String typeOn;



    private String room;



    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        TYPE

    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
