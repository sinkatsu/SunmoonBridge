package com.example.sunmoonbridge;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sunmoonbridge.favorites.FavoritesViewActivity;
import com.example.sunmoonbridge.ui.DirectChat.DirectChatFragment;
import com.example.sunmoonbridge.ui.Help.HelpFragment;
import com.example.sunmoonbridge.ui.KnowledgeFragment.KnowledgeFragment;
import com.example.sunmoonbridge.ui.Trade.TradeFragment;
import com.example.sunmoonbridge.ui.home.HomeFragment;
import com.example.sunmoonbridge.ui.mypage.MypageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private HomeFragment homeFragment = new HomeFragment();
    private DirectChatFragment directChatFragment = new DirectChatFragment();
    private HelpFragment helpFragment = new HelpFragment();
    private KnowledgeFragment knowledgeFragment = new KnowledgeFragment();
    private TradeFragment tradeFragment = new TradeFragment();

    Intent it;
    String UID, nickname;
    StudentMembars sm;

    DrawerLayout drawerLayout;
    View nav_header_view;
    NavigationView navigationView;
    ImageView imageIcon;
    TextView tv_id_view, tv_nk_view;
    Toolbar toolbar;

    DatabaseReference root,root2;
    ArrayList<String> nodeHelplist= new ArrayList<>();
    ArrayList<String> nodeTradelist= new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);

        Intent intent = new Intent(MainActivity.this,Animation.class);
        startActivity(intent);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UID = firebaseAuth.getUid();

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("홈");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_draw_page);

        nav_header_view = navigationView.inflateHeaderView(R.layout.nav_header);

        imageIcon = nav_header_view.findViewById(R.id.myicon_main_page);
        tv_id_view = nav_header_view.findViewById(R.id.tv_nav_head_user_id);
        tv_nk_view = nav_header_view.findViewById(R.id.tv_nav_head_user_nick_name);

        // navigation의 메뉴를 눌렀을 때의 처리
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                if(item.getItemId() == R.id.nav_my_page){ // 마이 페이지를 누른 경우
                    Intent intent = new Intent(getApplicationContext(), MypageActivity.class);
                    startActivity(intent);
                }else if (item.getItemId() == R.id.nav_setting){ // 설정을 누른경우
                    Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                }else if (item.getItemId() == R.id.nav_Favorites) { // 즐겨찾기를 누른경우
                    Intent intent = new Intent(getApplicationContext(), FavoritesViewActivity.class);
                    intent.putExtra("id",UID);
                    intent.putExtra("nickname",nickname);
                    startActivity(intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // 통지를 보내기 위한 token발급 및 갱신
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Fetching FCM registration token failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String token = task.getResult();
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                        root.child("User").child(mAuth.getUid()).child("profile").child("myToken").setValue(token);
                    }
                });

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, homeFragment).commitAllowingStateLoss();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new ItemSeletedListner());

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
                    nickname = sm.getNickname();

                    // 유저의 프로필 아이콘을 습득
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl("gs://navigationtempchattest.appspot.com");
                    storageReference.child("profile").child(sm.getProfile_picture_path()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(imageIcon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //이미지 로드 실패시
                            //Toast.makeText(getApplicationContext(), "아이콘 로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    class ItemSeletedListner implements BottomNavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (item.getItemId()){
                case R.id.navigation_home:
                    toolbar.setTitle("Home");
                    transaction.replace(R.id.nav_host_fragment, homeFragment).commitAllowingStateLoss();
                    break;

                case R.id.navigation_trade:
                    toolbar.setTitle("Trade");
                    transaction.replace(R.id.nav_host_fragment, tradeFragment).commitAllowingStateLoss();
                    Bundle bundle2=new Bundle();
                    bundle2.putString("id",UID);
                    bundle2.putString("nickname",nickname);
                    tradeFragment.setArguments(bundle2);
                    break;

                case R.id.navigation_help:
                    toolbar.setTitle("Help/Help Repuest");
                    transaction.replace(R.id.nav_host_fragment, helpFragment).commitAllowingStateLoss();
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("id",UID);
                    bundle3.putString("nickname",nickname);
                    helpFragment.setArguments(bundle3);
                    break;

                case R.id.navigation_knowledge:
                    toolbar.setTitle("Knowledge");
                    transaction.replace(R.id.nav_host_fragment, knowledgeFragment).commitAllowingStateLoss();
                    Bundle bundle4 = new Bundle();
                    bundle4.putString("id",UID);
                    bundle4.putString("nickname",nickname);
                    knowledgeFragment.setArguments(bundle4);
                    break;

                case R.id.navigation_direct_message:
                    toolbar.setTitle("Direct Chat");
                    transaction.replace(R.id.nav_host_fragment, directChatFragment).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때의 처리
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume(){
        super.onResume();
        loadUserIconAndID();
    }

    // 유정의 아이디와 아이콘을 습득하여 navView에 표시하는 메서드
    public void loadUserIconAndID(){
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

                    tv_id_view.setText(sm.getEmail());
                    tv_nk_view.setText(sm.getNickname());

                    // 유저의 프로필 아이콘을 습득
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl("gs://navigationtempchattest.appspot.com");
                    storageReference.child(sm.getProfile_picture_path()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(imageIcon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //이미지 로드 실패시
                            //Toast.makeText(getApplicationContext(), "아이콘 로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

}