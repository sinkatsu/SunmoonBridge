package com.example.sunmoonbridge.ui.mypage;

import android.content.Context;
import android.content.Intent;
import android.content.PeriodicSync;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.sunmoonbridge.R;
import com.example.sunmoonbridge.StudentMembars;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MypageActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String UID;
    StudentMembars sm;

    TextView userNikcname, userMajor, userSTNum, userContury, userSpecialty, userComment;
    Button profile_edit_bt;
    ImageView userIcon;
    private ProgressBar progressBar;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference root = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_include_my_page);

        Toolbar toolbar = findViewById(R.id.mypage_toolbar);
        toolbar.setTitle("My Page");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 현재 로그인 하고 있는 유저의 고유 ID를 습득
        UID = firebaseAuth.getUid();

        // firebase에 저장되어 있는 유저의 프로필 정보를 습득하여 표시하기 위한 Interface
        userIcon = (ImageView)findViewById(R.id.iv_mypage_profile_icon);
        userNikcname = (TextView)findViewById(R.id.tv_mypage_nickname);
        userMajor = (TextView)findViewById(R.id.tv_mypage_major);
        userSTNum = (TextView)findViewById(R.id.tv_mypage_stnum);
        userContury = (TextView)findViewById(R.id.tv_mypage_contury);
        userSpecialty = (TextView)findViewById(R.id.tv_mypage_specailty);
        userComment = (TextView)findViewById(R.id.tv_mypage_comment);
        profile_edit_bt = (Button)findViewById(R.id.bt_mypage_profile_edit);
        progressBar = findViewById(R.id.mypageProgressBar);

        // 유저의 프로필데이터를 습득
        loadMyProfile();

        profile_edit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MypageDditProfileActivity.class));
            }
        });
    }

    // toolbar 의 item을 선택하였을 때의 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: // toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadMyProfile(){
        root.child("User").child(UID).child("profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Error Getting UserData",Toast.LENGTH_SHORT).show();
                }else{
                    // 프로필 데이터를 습득하여 화면에 출력
                    sm = (StudentMembars)task.getResult().getValue(StudentMembars.class);
                    userNikcname.setText(sm.getNickname());
                    userMajor.setText(sm.getMajor());
                    userSTNum.setText(sm.getStudent_number());
                    userContury.setText(sm.getContury());
                    userSpecialty.setText(sm.getSpecialty());
                    userComment.setText(sm.getComment());

                    // 유저의 프로필 아이콘을 습득
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl("gs://navigationtempchattest.appspot.com");
                    storageReference.child(sm.getProfile_picture_path()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(userIcon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //이미지 로드 실패시
                            Toast.makeText(getApplicationContext(), "아이콘 로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void onResume(){
        super.onResume();
        loadMyProfile();
    }

}
