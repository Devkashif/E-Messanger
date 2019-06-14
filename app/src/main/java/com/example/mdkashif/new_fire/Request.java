package com.example.mdkashif.new_fire;

public class Request {

    private String Name;
    private String Status;
    private String Thumb_image;
    private String Accept, Decline;


    public  Request(){

    }


    public Request(String name, String status, String thumb_image, String accept, String decline) {
        Name = name;
        Status = status;
        Thumb_image = thumb_image;
        Accept = accept;
        Decline = decline;

    }

    public String getAccept() {
        return Accept;
    }

    public void setAccept(String accept) {
        Accept = accept;
    }

    public String getDecline() {
        return Decline;
    }

    public void setDecline(String decline) {
        Decline = decline;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getThumb_image() {
        return Thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        Thumb_image = thumb_image;
    }
}
