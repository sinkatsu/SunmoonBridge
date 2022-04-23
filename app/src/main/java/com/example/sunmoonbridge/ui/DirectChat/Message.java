package com.example.sunmoonbridge.ui.DirectChat;

import android.graphics.Bitmap;

public class Message {
    String userID;
    String nickname;
    String msg;
    String date;
    String time;
    String image;

    public Message(){ }

    public Message(String userID,String nName, String message, String date, String time, String image){
        this.userID = userID;
        this.nickname = nName;
        this.msg = message;
        this.date = date;
        this.time = time;
        this.image = image;
    }

    public String getNickname(){return this.nickname;}
    public String getMsg(){return this.msg;}
    public String getDate(){return this.date;}
    public String getTime(){return this.time;}
    public String getImage(){return this.image;}
    public String getUserID(){return this.userID;}
}
