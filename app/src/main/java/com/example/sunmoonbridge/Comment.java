package com.example.sunmoonbridge;

public class Comment {

    String commentNickname;
    String commentMsg;
    String commentDate;

    public Comment(){}

    public Comment(String commentNickname, String commentMsg, String commentDate){
        this.commentNickname = commentNickname;
        this.commentMsg = commentMsg;
        this.commentDate = commentDate;
    }

    public String getCommentNickname(){return this.commentNickname; }
    public String getCommentMsg(){return this.commentMsg; }
    public String getCommentDate(){return this.commentDate; }
}
