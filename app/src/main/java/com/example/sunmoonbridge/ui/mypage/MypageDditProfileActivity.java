package com.example.sunmoonbridge.ui.mypage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.sunmoonbridge.R;
import com.example.sunmoonbridge.StudentMembars;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MypageDditProfileActivity extends AppCompatActivity {

    ImageView iv_edit_icon;
    Button bt_select_icon;
    EditText et_nickname, et_major, et_stNum, et_country, et_specialty, et_comment;
    private ProgressBar progressBar;

    String icon_path = null, email = null, nickname, major, stNum, country, specialty, comment, token, notistate;
    int postCount;
    StudentMembars sm;
    Uri nowIconUri = null;
    String childName = null;
    boolean result = false;

    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_edit_myprofile);

        // 현제 로그인하고 있는 유저의 고유 ID를 습득
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UID = firebaseAuth.getUid();

        // toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar_mypro_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        // 화면의 widget 습득
        iv_edit_icon = findViewById(R.id.iv_editpro_icon);
        bt_select_icon = findViewById(R.id.bt_editpro_icon_select);

        et_nickname = findViewById(R.id.et_editpro_nickname);
        et_major = findViewById(R.id.et_editpro_major);
        et_stNum = findViewById(R.id.et_editpro_studentNumbar);
        et_country = findViewById(R.id.et_editpro_country);
        et_specialty = findViewById(R.id.et_editpro_specialty);
        et_comment = findViewById(R.id.et_editpro_comment);

        progressBar = findViewById(R.id.profileUpdateProgressBar);

        // 로그인하고 있는 유저의 프로필 정보를 습득
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference root = firebaseDatabase.getReference();
        root.child("User").child(UID).child("profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Error Getting UserData",Toast.LENGTH_SHORT).show();
                }else{
                    // 프로필 데이터를 습득하여 화면에 출력
                    sm = (StudentMembars)task.getResult().getValue(StudentMembars.class);

                    email = sm.getEmail();
                    icon_path = sm.getProfile_picture_path();
                    childName = sm.getProfile_picture_path();

                    nickname = sm.getNickname();
                    et_nickname.setText(nickname);

                    token = sm.getMyToken();
                    notistate = sm.getNotiState();
                    postCount = sm.getPostCount();

                    major = sm.getMajor();
                    if (major.equals("No Data")){
                        et_major.setText(null);
                    }else {
                        et_major.setText(major);
                    }

                    stNum = sm.getStudent_number();
                    if (stNum.equals("No Data")){
                        et_stNum.setText(null);
                    }else {
                        et_stNum.setText(stNum);
                    }

                    country = sm.getContury();
                    if (country.equals("No Data")){
                        et_country.setText(null);
                    }else {
                        et_country.setText(country);
                    }

                    specialty = sm.getSpecialty();
                    if (specialty.equals("No Data")){
                        et_specialty.setText(null);
                    }else {
                        et_specialty.setText(specialty);
                    }

                    comment = sm.getComment();
                    if (comment.equals("No Data")){
                        et_comment.setText(null);
                    }else {
                        et_comment.setText(comment);
                    }

                    // 유저의 프로필 아이콘을 습득
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl("gs://navigationtempchattest.appspot.com");
                    storageReference.child(icon_path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(iv_edit_icon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //이미지 로드 실패시
                            Toast.makeText(getApplicationContext(), "아이콘 로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        // 아이콘 사진 선택 버튼을누르면 갤러리에 이동하여 선택하도록 한다.
        bt_select_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 1);
            }
        });

    }

    // 이름을 작성하기 위한 메서드
    public String setImgFileName(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat Format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        String id = Format.format(date);
        return id;
    }

    // 메뉴 활성화
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.prifile_edit_menu,menu);
        return true;
    }

    // 사진을 겔러리에서 선택하고 나녀왔을 때의 동작 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {// 사진을 겔러리에서 가지고 왔다면
                progressBar.setVisibility(View.VISIBLE);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                nowIconUri = data.getData();
                try{
                    // 설정한 옵션으로 로드하여 Bitmap 로 변환
                    Bitmap img = BitmapFactory.decodeStream(getContentResolver().openInputStream(nowIconUri),null,options);
                    iv_edit_icon.setImageBitmap(img); // 현재 화면에 출력
                    childName = "UserIcon/" + sm.getNickname() + setImgFileName() + nowIconUri.getLastPathSegment();
                }catch(Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            } else if(resultCode == RESULT_CANCELED) { // 어떤 사진도 선택하지 않았다면
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    // toolbar 의 item을 선택하였을 때의 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        progressBar.setVisibility(View.VISIBLE);
        switch (item.getItemId()){
            case android.R.id.home: // toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            case R.id.saveEditDataMenu: // 프로필 변경 저장 버튼을 누렀을 때
                item.setEnabled(false);
                nickname = et_nickname.getText().toString();
                if (TextUtils.isEmpty(nickname)){
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                major = et_major.getText().toString();
                if (TextUtils.isEmpty(major)){
                    major = "No Data";
                }
                stNum = et_stNum.getText().toString();
                if (TextUtils.isEmpty(stNum)){
                    stNum = "No Data";
                }
                country = et_country.getText().toString();
                if (TextUtils.isEmpty(country)){
                    country = "No Data";
                }
                specialty = et_specialty.getText().toString();
                if (TextUtils.isEmpty(specialty)){
                    specialty = "No Data";
                }
                comment = et_comment.getText().toString();
                if (TextUtils.isEmpty(comment)){
                    comment = "No Data";
                }
                if (!childName.equals(icon_path) && !icon_path.equals("UserIcon/DefultIcon.png")) { // 과거의 아이콘과 현재 습득하고 있는 아이콘이 다를 때
                    // 갱신한 정보를 RealTimeDatabase에 저장
                    updateUserProfile(email, childName, nickname, major, stNum, country, specialty, comment,token,notistate,postCount);
                    // 신규 아이콘을 storage에 업로드 및 과거의 아이콘 삭제
                    deleteOldIcon(icon_path);
                    updateUserIcon(childName);
                }else if (!childName.equals(icon_path) && icon_path.equals("UserIcon/DefultIcon.png")){ // 과거의아이콘이 디폴트 아이콘 일 때
                    // 갱신한 정보를 RealTimeDatabase에 저장
                    updateUserProfile(email, childName, nickname, major, stNum, country, specialty, comment,token,notistate,postCount);
                    // 신규 아이콘을 storage에 업로드
                    updateUserIcon(childName);
                }else if (childName.equals(icon_path)){ // 과거의 아이콘과 현재 습득하고 있는 아이콘이 같을 때
                    // 갱신한 정보를 RealTimeDatabase에 저장
                    updateUserProfile(email, childName, nickname, major, stNum, country, specialty, comment,token,notistate,postCount);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUserIcon(String newIconPath){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference stroageRef = storage.getReference();

        StorageReference imageRef = stroageRef.child(childName);
        UploadTask uploadTask = imageRef.putFile(nowIconUri);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                //Toast.makeText(getApplicationContext(), "Upload is image: "+progress+"%", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload icon Exception!!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Upload icon Complate", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void deleteOldIcon(String oldIconPath){
        // 과거의 아이콘을 삭제
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference stroageRef = storage.getReference();
        StorageReference desertRef = stroageRef.child(oldIconPath);
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Delete old icon Complate", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Delete old icon Exception!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile(String email, String profile_picture_path, String nickname,
                                   String major, String student_number, String contury,
                                   String specialty, String comment, String myToken, String myNotiState, int postCount){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference root = firebaseDatabase.getReference();
        StudentMembars sm = new StudentMembars(email, profile_picture_path, nickname, major, student_number, contury, specialty, comment, myToken, myNotiState, postCount);
        root.child("User").child(UID).child("profile").setValue(sm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"프로필 변경 완료",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
