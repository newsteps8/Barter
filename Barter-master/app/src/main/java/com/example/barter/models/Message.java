package com.example.barter.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Message {
    String messageText;
    String sender;
    String time;
    String receiverId;
    String senderName;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


    public Message() {
    }

    public Message(String messageText, String senderName) {
        this.messageText = messageText;
        this.senderName = senderName;
    }

    public Message(String messageText, String sender, String time, String receiverId, String senderName) {
        this.messageText = messageText;
        this.sender = sender;
        this.time = time;
        this.receiverId = receiverId;
        this.senderName = senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("messageText", messageText);
        result.put("time", time);
        result.put("receiver", receiverId);
        result.put("senderName", senderName);
        return result;
    }

}
