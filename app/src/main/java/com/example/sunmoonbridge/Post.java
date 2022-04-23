package com.example.sunmoonbridge;

import java.io.Serializable;

public class Post implements Serializable {
    private String postid;          // 게시물 고유 ID
    private int officialnumber;     // 몇번째 개시물
    private String nickname;        // 게시자 이름
    String title;           // 제목
    String note;            // 내용
    private String date;            // 개시 날짜
    private String dateEnd;         // 게시 기한
    private String division;        // (give, take)구분
    private int photonum;           // 게시물이 가지는 사진 겟수
    private String userid;          // 이 게시물을 등록한 사람의 UID
    private String dlUrlsingle;     // 사진의 다운로드를 위한 경로 데이터
    private int favorite;           // 이 게시물의 즐겨찾기 등록수
    private int completecheck;
    private String bestAnswer;

    public Post(String nickname){
        this.nickname=nickname;
    }

    public Post(String title, String nickname){
        this.nickname=nickname;
        this.title=title;
    }

    public Post(String pid,String title,String nickname,String note,String date,int num,String userid){
        this.postid=pid;
        this.title=title;
        this.nickname=nickname;
        this.note=note;
        this.date = date;
        photonum=num;
        this.userid = userid;
    }

    public Post(String pid,String title,String nickname,String note,String date,String division,int num,String userid,int complete){
        this.postid=pid;
        this.title=title;
        this.nickname=nickname;
        this.note=note;
        this.date = date;
        this.division= division;
        photonum=num;
        this.userid = userid;
        this.completecheck=complete;
    }

    public Post(String pid,String title,String nickname,String note,String date,String dateEnd,String division,int num,String userid,int complete){
        this.postid=pid;
        this.title=title;
        this.nickname=nickname;
        this.note=note;
        this.date = date;
        this.dateEnd = dateEnd;
        this.division= division;
        photonum=num;
        this.userid = userid;
        this.completecheck=complete;
    }

    public Post(){}

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) { this.postid = postid; }

    public int getPhotonum() { return photonum; }

    public String getNickname() {
        return nickname;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public String getUserid(){return userid;}

    public String getDlUrlsingle() {
        return dlUrlsingle;
    }

    public void setDlUrlsingle(String dlUrlsingle) {
        this.dlUrlsingle = dlUrlsingle;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getOfficialnumber() {
        return officialnumber;
    }

    public void setOfficialnumber(int officialnumber) {
        this.officialnumber = officialnumber;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getCompletecheck() {
        return completecheck;
    }

    public void setCompletecheck(int completecheck) {
        this.completecheck = completecheck;
    }

    public String getBestAnswer() {
        return bestAnswer;
    }

    public void setBestAnswer(String bestAnswer) {
        this.bestAnswer = bestAnswer;
    }
}
