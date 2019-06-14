package com.example.mdkashif.new_fire;

public class All_user_adapter {

    String Name;
    String Status;
    String Image;
    String Thumb_image;

    public All_user_adapter(){

       }

    public All_user_adapter(String Name, String Status, String Image, String Thumb_image){
        this.Name=Name;
        this.Status=Status;
        this.Image=Image;
        this.Thumb_image=Thumb_image;

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

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


    public String getThumb_image() {
        return Thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        Thumb_image = thumb_image;
    }
}