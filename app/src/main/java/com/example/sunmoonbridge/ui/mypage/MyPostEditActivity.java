package com.example.sunmoonbridge.ui.mypage;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.example.sunmoonbridge.Comment;
import com.example.sunmoonbridge.CommentAdapter;
import com.example.sunmoonbridge.CommentListView;
import com.example.sunmoonbridge.Post;
import com.example.sunmoonbridge.R;
import com.example.sunmoonbridge.StudentMembars;
import com.example.sunmoonbridge.ui.Help.PicassoSampleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class MyPostEditActivity extends AppCompatActivity {

    TextView tvnickname, tvdata, tvtitle,tvdeadline,tvnote;
    Button bt_comment, bt_message;
    SwitchCompat switchCompat;
    TextView complete;
    ImageView imageView1,imageView2,imageView3,imageView4,imageView5,imageView6,imageView7,imageView8,imageView9,imageView10, icon;
    Intent intent;
    Toolbar toolbar;
    DatePickerDialog.OnDateSetListener callbackMethod;
    StudentMembars sm;

    Post post;
    CommentListView listView;
    ArrayList<Comment> comments;
    CommentAdapter madapter;
    FirebaseDatabase firebasedb;
    DatabaseReference root;
    String posttype, dateend = "";

    private String usernickname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);

        ActivityCompat.requestPermissions(MyPostEditActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(MyPostEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        intent = getIntent();
        final String nickname = intent.getStringExtra("nickname");
        final String userid = intent.getStringExtra("Userid");
        post = (Post) intent.getSerializableExtra("detail");
        posttype = intent.getStringExtra("type of post");

        toolbar = findViewById(R.id.post_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Post View");

        tvnickname=findViewById(R.id.tv_nickname);
        tvdata=findViewById(R.id.tv_date2);
        tvtitle=findViewById(R.id.tv_title2);
        tvdeadline= findViewById(R.id.tv_deadline);
        tvnote=findViewById(R.id.tv_note);
        bt_comment=findViewById(R.id.bt_comment);
        bt_message=findViewById(R.id.go_chat_button);
        bt_message.setVisibility(View.GONE);
        switchCompat=findViewById(R.id.sw_completecheck);
        complete=findViewById(R.id.completecheck);

        icon = findViewById(R.id.profile1);
        imageView1=findViewById(R.id.imageView11);
        imageView2=findViewById(R.id.imageView12);
        imageView3=findViewById(R.id.imageView13);
        imageView4=findViewById(R.id.imageView14);
        imageView5=findViewById(R.id.imageView18);
        imageView6=findViewById(R.id.imageView17);
        imageView7=findViewById(R.id.imageView19);
        imageView8=findViewById(R.id.imageView20);
        imageView9=findViewById(R.id.imageView21);
        imageView10=findViewById(R.id.imageView22);

        callbackMethod = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                dateend = year + "." + (monthOfYear+1) + "." + dayOfMonth;
                Toast.makeText(MyPostEditActivity.this, dateend, Toast.LENGTH_SHORT).show();
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference root = firebaseDatabase.getReference();
                tvdeadline.setText(dateend);
                root.child("PostData").child(posttype).child(post.getPostid()).child("postdetail").child("dateEnd").setValue(dateend);
            }
        };

        //★ 객체로 가져와서 표시
        tvnickname.setText(post.getNickname());
        tvdata.setText(post.getDate());
        tvtitle.setText(post.getTitle());
        tvnote.setText(post.getNote());
        tvdeadline.setText(post.getDateEnd());
        complete.setVisibility(View.VISIBLE);

        if(posttype.contains("Knowledge")){
            switchCompat.setText("해결여부");
        }else if(posttype.contains("Trade")){
            switchCompat.setText("거래여부");
        }else {
            switchCompat.setText("접수여부");
        }

        if(post.getCompletecheck()==1){
            switchCompat.setChecked(true);
            complete.setBackgroundResource(R.drawable.frame_style2);
            if(posttype.contains("Knowledge")){
                complete.setText("해결");
            }else if(posttype.contains("Trade")){
                complete.setText("거래종료");
            }else {
                complete.setText("접수종료");
            }
        }else{
            complete.setBackgroundResource(R.drawable.frame_style);
            if(posttype.contains("Knowledge")){
                complete.setText("미해결");
            }else if(posttype.contains("Trade")){
                complete.setText("거래접수중");
            }else {
                complete.setText("접수중");
           }
        }


        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCompat.isChecked()){
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("PostData").child(posttype).child(post.getPostid()).child("postdetail").child("completecheck");
                    root.setValue(1);
                    complete.setBackgroundResource(R.drawable.frame_style2);
                    if(posttype.contains("Knowledge")){
                        complete.setText("해결");
                    }else if(posttype.contains("Trade")){
                        complete.setText("거래종료");
                    }else {
                        complete.setText("접수종료");
                    }
                }else{
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("PostData").child(posttype).child(post.getPostid()).child("postdetail").child("completecheck");
                    root.setValue(0);
                    complete.setBackgroundResource(R.drawable.frame_style);
                    if(posttype.contains("Knowledge")){
                        complete.setText("미해결");
                    }else if(posttype.contains("Trade")){
                        complete.setText("거래접수중");
                    }else {
                        complete.setText("접수중");
                    }
                }
            }
        });


        firebasedb = FirebaseDatabase.getInstance();
        // 변경 포인트
        root = firebasedb.getReference().child("PostData").child(posttype).child(post.getPostid()).child("chat");


        ArrayList<String> urls=new ArrayList<>();
        String s = post.getDlUrlsingle();
        StringTokenizer st=new StringTokenizer(s,"***");
        for(int j=0;j<post.getPhotonum();j++){
            urls.add(st.nextToken());
        }


        if(post.getPhotonum()==10){
            Picasso.get().load(urls.get(0)).resize(540,540).centerCrop().into(imageView1);
            Picasso.get().load(urls.get(1)).resize(540,540).centerCrop().into(imageView2);
            Picasso.get().load(urls.get(2)).resize(540,540).centerCrop().into(imageView3);
            Picasso.get().load(urls.get(3)).resize(540,540).centerCrop().into(imageView4);
            Picasso.get().load(urls.get(4)).resize(540,540).centerCrop().into(imageView5);
            Picasso.get().load(urls.get(5)).resize(540,540).centerCrop().into(imageView6);
            Picasso.get().load(urls.get(6)).resize(540,540).centerCrop().into(imageView7);
            Picasso.get().load(urls.get(7)).resize(540,540).centerCrop().into(imageView8);
            Picasso.get().load(urls.get(8)).resize(540,540).centerCrop().into(imageView9);
            Picasso.get().load(urls.get(9)).resize(540,540).centerCrop().into(imageView10);
        }
        else if(post.getPhotonum()==9){
            Picasso.get().load(urls.get(0)).resize(540,540).centerCrop().into(imageView1);
            Picasso.get().load(urls.get(1)).resize(540,540).centerCrop().into(imageView2);
            Picasso.get().load(urls.get(2)).resize(540,540).centerCrop().into(imageView3);
            Picasso.get().load(urls.get(3)).resize(540,540).centerCrop().into(imageView4);
            Picasso.get().load(urls.get(4)).resize(540,540).centerCrop().into(imageView5);
            Picasso.get().load(urls.get(5)).resize(540,540).centerCrop().into(imageView6);
            Picasso.get().load(urls.get(6)).resize(540,540).centerCrop().into(imageView7);
            Picasso.get().load(urls.get(7)).resize(540,540).centerCrop().into(imageView8);
            Picasso.get().load(urls.get(7)).resize(540,540).centerCrop().into(imageView9);
        }
        else if(post.getPhotonum()==8){
            Picasso.get().load(urls.get(0)).resize(540,540).centerCrop().into(imageView1);
            Picasso.get().load(urls.get(1)).resize(540,540).centerCrop().into(imageView2);
            Picasso.get().load(urls.get(2)).resize(540,540).centerCrop().into(imageView3);
            Picasso.get().load(urls.get(3)).resize(540,540).centerCrop().into(imageView4);
            Picasso.get().load(urls.get(4)).resize(540,540).centerCrop().into(imageView5);
            Picasso.get().load(urls.get(5)).resize(540,540).centerCrop().into(imageView6);
            Picasso.get().load(urls.get(6)).resize(540,540).centerCrop().into(imageView7);
            Picasso.get().load(urls.get(7)).resize(540,540).centerCrop().into(imageView8);
        }
        else if(post.getPhotonum()==7){
            Picasso.get().load(urls.get(0)).resize(540,540).centerCrop().into(imageView1);
            Picasso.get().load(urls.get(1)).resize(540,540).centerCrop().into(imageView2);
            Picasso.get().load(urls.get(2)).resize(540,540).centerCrop().into(imageView3);
            Picasso.get().load(urls.get(3)).resize(540,540).centerCrop().into(imageView4);
            Picasso.get().load(urls.get(4)).resize(540,540).centerCrop().into(imageView5);
            Picasso.get().load(urls.get(5)).resize(540,540).centerCrop().into(imageView6);
            Picasso.get().load(urls.get(6)).resize(540,540).centerCrop().into(imageView7);
        }
        else if(post.getPhotonum()==6){
            Picasso.get().load(urls.get(0)).resize(540,540).centerCrop().into(imageView1);
            Picasso.get().load(urls.get(1)).resize(540,540).centerCrop().into(imageView2);
            Picasso.get().load(urls.get(2)).resize(540,540).centerCrop().into(imageView3);
            Picasso.get().load(urls.get(3)).resize(540,540).centerCrop().into(imageView4);
            Picasso.get().load(urls.get(4)).resize(540,540).centerCrop().into(imageView5);
            Picasso.get().load(urls.get(5)).resize(540,540).centerCrop().into(imageView6);
        }
        else if(post.getPhotonum()==5){
            Picasso.get().load(urls.get(0)).resize(540,540).centerCrop().into(imageView1);
            Picasso.get().load(urls.get(1)).resize(540,540).centerCrop().into(imageView2);
            Picasso.get().load(urls.get(2)).resize(540,540).centerCrop().into(imageView3);
            Picasso.get().load(urls.get(3)).resize(540,540).centerCrop().into(imageView4);
            Picasso.get().load(urls.get(4)).resize(540,540).centerCrop().into(imageView5);
        }
        else if(post.getPhotonum()==4){
            Picasso.get().load(urls.get(0)).resize(540,540).centerCrop().into(imageView1);
            Picasso.get().load(urls.get(1)).resize(540,540).centerCrop().into(imageView2);
            Picasso.get().load(urls.get(2)).resize(540,540).centerCrop().into(imageView3);
            Picasso.get().load(urls.get(3)).resize(540,540).centerCrop().into(imageView4);
        }
        else if(post.getPhotonum()==3){
            Picasso.get().load(urls.get(0)).resize(540,540).centerCrop().into(imageView1);
            Picasso.get().load(urls.get(1)).resize(540,540).centerCrop().into(imageView2);
            Picasso.get().load(urls.get(2)).resize(400,400).centerCrop().into(imageView3);
        }
        else if(post.getPhotonum()==2){
            Picasso.get().load(urls.get(0)).resize(540,540).centerCrop().into(imageView1);
            Picasso.get().load(urls.get(1)).resize(540,540).centerCrop().into(imageView2);
        }
        else if(post.getPhotonum()==1){
            Picasso.get().load(urls.get(0)).resize(1400,900).centerInside().into(imageView1);
        }

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String im = urls.get(0);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                //animation
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                savealert(urls.get(0));
                return true;
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String im = urls.get(1);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                //animation
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                savealert(urls.get(1));
                return true;
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String im = urls.get(2);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                savealert(urls.get(2));
                return true;
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String im = urls.get(3);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                savealert(urls.get(3));
                return true;
            }
        });
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String im = urls.get(4);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                savealert(urls.get(4));
                return true;
            }
        });
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String im = urls.get(5);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                savealert(urls.get(5));
                return true;
            }
        });
        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String im = urls.get(6);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                savealert(urls.get(6));
                return true;
            }
        });
        imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String im = urls.get(7);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                savealert(urls.get(7));
                return true;
            }
        });
        imageView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String im = urls.get(8);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView9.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                savealert(urls.get(8));
                return true;
            }
        });
        imageView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String im = urls.get(9);
                Intent intent = new Intent(MyPostEditActivity.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MyPostEditActivity.this, view, "image1");
                startActivity(intent,options.toBundle());
            }
        });
        imageView10.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                savealert(urls.get(9));
                return true;
            }
        });

        //아이콘 읽어오기
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootUser = firebaseDatabase.getReference();
        rootUser.child("User").child(post.getUserid()).child("profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Error Getting UserData",Toast.LENGTH_SHORT).show();
                }else{
                    // 프로필 데이터를 습득하여 화면에 출력
                    sm = (StudentMembars)task.getResult().getValue(StudentMembars.class);

                    // 유저의 프로필 아이콘을 습득
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl("gs://navigationtempchattest.appspot.com");
                    storageReference.child(sm.getProfile_picture_path()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(icon);
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

        listView = findViewById(R.id.listview_commentarea);
        comments = new ArrayList<Comment>();
        madapter = new CommentAdapter(getApplicationContext(),comments);//今のレイアウトに作られたLayoutInflater（リソースレイアウトを開始化させるもの）を渡す
        listView.setAdapter(madapter);//今の時点で入っているDBのデータを出力
        madapter.notifyDataSetChanged();

        // 뎃글 읽어오기
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Comment comment = snapshot.getValue(Comment.class);//Message.classの意味　クラス形態でデータをもらう//★参照先に該当のオブジェクトではなく別のもの（ノード）があるとアプリ落ちる
                comments.add(comment);//arraylistに新しいメッセージを保存
                madapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 뎃글을 등록하는 엑션 버튼
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setnickname(nickname);
                alertcheck();
            }
        });
    }

    // toolbar 의 item을 선택하였을 때의 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            case R.id.popop_extend_menu:
                //Toast.makeText(this, "popop_extend_menu", Toast.LENGTH_SHORT).show();
                final Calendar date = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
                dialog.setMessage("Please set the extension period.");
                dialog.setCancelable(false);
                dialog.getDatePicker().setMinDate(new Date().getTime());
                dialog.show();
                return true;
            case R.id.popop_delete_menu:
                //Toast.makeText(this, "popop_delete_menu", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("")
                        .setMessage("Are you sure you want to delete it?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<String> urls = new ArrayList<String>();
                                ArrayList<String> filenames = new ArrayList<String>();
                                if (post.getDlUrlsingle() != null) {
                                    String s = post.getDlUrlsingle();
                                    try {
                                        StringTokenizer st = new StringTokenizer(s, "***");
                                        for (int j = 0; j < post.getPhotonum(); j++) {
                                            urls.add(st.nextToken());
                                        }
                                    } catch (Exception e) {
                                        //Toast.makeText(getApplicationContext(), "Exception1"+e, Toast.LENGTH_SHORT).show();
                                    }
                                    for (String file : urls) {
                                        try {
                                            String[] files = file.split("2F",0);
                                            //Toast.makeText(getApplicationContext(), files[1], Toast.LENGTH_SHORT).show();
                                            String[] fl = files[1].split(String.valueOf(".jpg"),0);
                                            StorageReference stroageRef = FirebaseStorage.getInstance().getReference();
                                            StorageReference targetFile = stroageRef.child("post images/"+fl[0]+".jpg");
                                            targetFile.delete();
                                            //Toast.makeText(getApplicationContext(), filenames.get(0), Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            //Toast.makeText(getApplicationContext(), "Exception2"+e, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                //개시물을 Dealtime databaee 에서 삭제
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference root = firebaseDatabase.getReference();
                                root.child("PostData").child(posttype).child(post.getPostid()).removeValue();
                                finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        })
                        .setCancelable(false).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 메뉴 활성화
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popupmenu, menu);
        return true;
    }

    private void setnickname(String nick){
        usernickname=nick;
    }

    private String getUsernickname(){
        return usernickname;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    private void alertcheck(){
        final EditText editText = new EditText(getApplicationContext());
        editText.setHint("enter message here");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Comment")
                .setMessage("send message")
                .setView(editText)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendMessageToDB(editText.getText().toString());
                    }
                })
                .show();
    }

    private void savealert(String url){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyPostEditActivity.this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isExternalStorageWritable()) {
                    Uri uri = Uri.parse(url);
                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

                    request.setTitle("image is download Now");
                    request.setDescription("Android data download using DownloadManager.");
                    request.allowScanningByMediaScanner();
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/image/"+"/"+"salmankhan"+".png");
                    request.setMimeType("*/*");
                    downloadManager.enqueue(request);
                }
            }
        });
        dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void sendMessageToDB(String Message){//sendボタンを押したとき実行　引数でニックネーム
        String inputMessage = Message;
        Calendar calendar = Calendar.getInstance();//生成するのではなく持ってくるだけ　プログラムが実行されれば一個だけオブジェクトが生成される
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy.MM.dd");
        String date= sdf.format(calendar.getTime());
        Comment commentObject = new Comment(getUsernickname(),String.valueOf(inputMessage), date);//メッセージオブジェクトを作る
        DatabaseReference msgNode = root.push();//dbのルートノードの下につながるchildRef2の下に一個ノードを作るということ　pushはノード１つ作るという意味　★msgNodeの親ノードがchildRef2
        msgNode.setValue(commentObject);//ノードはオブジェクトを持つ

    }

}
