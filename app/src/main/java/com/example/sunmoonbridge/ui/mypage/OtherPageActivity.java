package com.example.sunmoonbridge.ui.mypage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.sunmoonbridge.ui.DirectChat.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OtherPageActivity extends AppCompatActivity {

    String TUID , myNick , UID;
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
        setContentView(R.layout.activity_include_othar_page);

        Toolbar toolbar = findViewById(R.id.other_toolbar);
        toolbar.setTitle("Other Page");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ????????? UID??? ?????????.
        Intent intent = getIntent();
        UID = intent.getStringExtra("MID");
        TUID = intent.getStringExtra("TID");
        myNick = intent.getStringExtra("MyNickName");

        // firebase??? ???????????? ?????? ????????? ????????? ????????? ???????????? ???????????? ?????? Interface
        userIcon = (ImageView)findViewById(R.id.iv_mypage_profile_icon);
        userNikcname = (TextView)findViewById(R.id.tv_mypage_nickname);
        userMajor = (TextView)findViewById(R.id.tv_mypage_major);
        userSTNum = (TextView)findViewById(R.id.tv_mypage_stnum);
        userContury = (TextView)findViewById(R.id.tv_mypage_contury);
        userSpecialty = (TextView)findViewById(R.id.tv_mypage_specailty);
        userComment = (TextView)findViewById(R.id.tv_mypage_comment);
        profile_edit_bt = (Button)findViewById(R.id.go_chat_button);
        profile_edit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), ChatActivity.class);
                it.putExtra("MID",UID);
                it.putExtra("TID",TUID);
                it.putExtra("TToken",sm.getMyToken());
                it.putExtra("MyNickName",myNick);
                it.putExtra("targetNickname",sm.getNickname());
                startActivity(it);
            }
        });

        progressBar = findViewById(R.id.mypageProgressBar);

    }

    // toolbar ??? item??? ??????????????? ?????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: // toolbar??? back??? ????????? ??? ??????
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadMyProfile(){
        root.child("User").child(TUID).child("profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Error Getting UserData",Toast.LENGTH_SHORT).show();
                }else{
                    // ????????? ???????????? ???????????? ????????? ??????
                    sm = (StudentMembars)task.getResult().getValue(StudentMembars.class);
                    userNikcname.setText(sm.getNickname());
                    userMajor.setText(sm.getMajor());
                    userSTNum.setText(sm.getStudent_number());
                    userContury.setText(sm.getContury());
                    userSpecialty.setText(sm.getSpecialty());
                    userComment.setText(sm.getComment());

                    // ????????? ????????? ???????????? ??????
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl("gs://navigationtempchattest.appspot.com");
                    storageReference.child(sm.getProfile_picture_path()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) { // ????????? ?????? ??????
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(userIcon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //????????? ?????? ?????????
                            Toast.makeText(getApplicationContext(), "????????? ?????? ??????", Toast.LENGTH_SHORT).show();
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
