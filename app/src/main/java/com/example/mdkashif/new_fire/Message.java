package com.example.mdkashif.new_fire;



public class Message {

    private String Message, Type;
    private long Time;
    private boolean Seen;
    private String from;

    public Message() {

    }

    public Message(String message, String type, long time, boolean seen, String from) {

        this.Message = message;
        this.Type = type;
        this.Time = time;
        this.Seen = seen;
        this.from = from;

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        this.Time = time;
    }

    public boolean isSeen() {
        return Seen;
    }

    public void setSeen(boolean seen) {
        this.Seen = seen;
    }
}