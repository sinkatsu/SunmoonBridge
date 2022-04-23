package com.example.sunmoonbridge.ui.DirectChat;

import android.net.Uri;

public class UsersRoom {
    String userID;
    String targetNickname;
    String targetToken;

    public UsersRoom(String UserID, String targetNickname, String targetToken){
        this.userID = UserID;
        this.targetNickname = targetNickname;
        this.targetToken = targetToken;
    }

    public String getUsersRoom(){
        return this.userID;
    }

    public String getTargetNickname(){
        return this.targetNickname;
    }

    public String getTargetToken(){
        return this.targetToken;
    }
}
