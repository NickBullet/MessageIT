package com.nickbullet.messageitmobile.other;

public class Message {
    private String fromName, message, time;
    private boolean isSelf;
 
    public Message() {
    }
 
    public Message(String fromName, String message, String time, boolean isSelf) {
        this.fromName = fromName;
        this.message = message;
        this.time = time;
        this.isSelf = isSelf;
    }
 
    public String getFromName() {
        return fromName;
    }
 
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
 
    public String getMessage() {
        return message;
    }
 
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTime() {
        return time;
    }
 
    public void setTime(String time) {
        this.time = time;
    }
 
    public boolean isSelf() {
        return isSelf;
    }
 
    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }
 
}
