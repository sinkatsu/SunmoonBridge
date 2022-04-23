package com.example.sunmoonbridge;

public class StudentMembars {
    String email;                 // ID
    String profile_picture_path;  // 프로필 사진 경로
    String nickname;              // 닉내임
    String major;                 // 전공
    String student_number;        // 학번
    String contury;               // 국가
    String specialty;             // 특기
    String comment;               // 자기소개
    String myToken;               // 알림을 받기 위한 자신의 주소
    String notiState;             // 알림수신 여부
    int postCount;                // 자용자가 가지는 개시물의 겟수

    public StudentMembars(){}

    public StudentMembars(String email, String profile_picture_path, String nickname,
                          String major, String student_number, String contury,
                          String specialty, String comment, String myToken, String notiState, int postCount){

        this.email = email;
        this.profile_picture_path = profile_picture_path;
        this.nickname = nickname;
        this.major = major;
        this.student_number = student_number;
        this.contury = contury;
        this.specialty = specialty;
        this.comment = comment;
        this.myToken = myToken;
        this.notiState = notiState;
        this.postCount = postCount;
    }

    public String getEmail() {
        return this.email;
    }

    public String getProfile_picture_path(){
        return this.profile_picture_path;
    }

    public String getNickname(){
        return this.nickname;
    }

    public String getMajor(){
        return this.major;
    }

    public String getStudent_number(){
        return this.student_number;
    }

    public String getContury(){
        return this.contury;
    }

    public String getSpecialty(){
        return this.specialty;
    }

    public String getComment(){
        return this.comment;
    }

    public String getMyToken(){return this.myToken;}

    public String getNotiState(){return this.notiState;}

    public int getPostCount(){return this.postCount;}
}
