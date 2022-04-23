package com.example.sunmoonbridge;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.sunmoonbridge.R;
import com.example.sunmoonbridge.ui.DirectChat.ChatActivity;
import com.example.sunmoonbridge.ui.Help.PicassoSampleActivity;
import com.example.sunmoonbridge.ui.mypage.OtherPageActivity;
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
import java.util.Calendar;
import java.util.StringTokenizer;

public class PostDetail extends AppCompatActivity {

    TextView tvnickname, tvdata, tvtitle,tvdeadline,tvnote;
    Button bt_comment, bt_go_chat;
    SwitchCompat switchCompat;
    TextView complete;
    ImageView imageView1,imageView2,imageView3,imageView4,imageView5,imageView6,imageView7,imageView8,imageView9,imageView10;
    Intent intent;
    Toolbar toolbar;
    ImageView icon;
    StudentMembars sm;

    CommentListView listView;
    ArrayList<Comment> comments;
    CommentAdapter madapter;
    FirebaseDatabase firebasedb;
    DatabaseReference root;
    String posttype;
    Post post;

    private String usernickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);

        ActivityCompat.requestPermissions(PostDetail.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(PostDetail.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

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
        bt_go_chat = findViewById(R.id.go_chat_button);
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

        //★ 객체로 가져와서 표시
        tvnickname.setText(post.getNickname());
        tvdata.setText(post.getDate());
        tvtitle.setText(post.getTitle());
        tvtitle.setSelected(true);
        tvnote.setText(post.getNote());
        tvdeadline.setText(post.getDateEnd());
        switchCompat.setVisibility(View.GONE);
        if(post.getCompletecheck()==1){

            if(posttype.contains("Knowledge")){
                complete.setText("해결");
            }else if(posttype.contains("Trade")){
                complete.setText("겨래종료");
            }else complete.setText("접수종료");
        }else {
            complete.setBackgroundResource(R.drawable.frame_style);
            if(posttype.contains("Knowledge")){
                complete.setText("미해결");
            }else if(posttype.contains("Trade")){
                complete.setText("거래접수중");
            }else {
                complete.setText("접수중");
            }
        }

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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                //animation
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                //animation
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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
                Intent intent = new Intent(PostDetail.this, PicassoSampleActivity.class);
                intent.putExtra("image",im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(PostDetail.this, view, "image1");
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


        // 아이콘을 습득
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
                    try {
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
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "이 유저는 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                        bt_go_chat.setVisibility(View.GONE);
                        icon.setVisibility(View.GONE);
                    }
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

        // 체팅 바로하기 버튼을 클릭한 경우
        bt_go_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), ChatActivity.class);
                it.putExtra("MID", userid);
                it.putExtra("TID",post.getUserid());
                it.putExtra("TToken",sm.getMyToken());
                it.putExtra("MyNickName",nickname);
                it.putExtra("targetNickname",sm.getNickname());
                startActivity(it);
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(PostDetail.this, "Touch Icon", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getApplicationContext(), OtherPageActivity.class);
                it.putExtra("MID",userid);
                it.putExtra("TID",post.getUserid());
                it.putExtra("MyNickName",nickname);
                startActivity(it);
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
        }
        return super.onOptionsItemSelected(item);
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
                        if(TextUtils.isEmpty(editText.getText().toString())){
                            Toast.makeText(PostDetail.this, "Enter a comment!!", Toast.LENGTH_SHORT).show();
                        }else {
                            sendMessageToDB(editText.getText().toString());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .show();
    }

    private void savealert(String url){
        AlertDialog.Builder dialog = new AlertDialog.Builder(PostDetail.this);
        dialog.setCancelable(false);
        dialog.setMessage("Are you going to download this image?").setPositiveButton("save", new DialogInterface.OnClickListener() {
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

    public void loadIcon(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference root = firebaseDatabase.getReference();
        root.child("User").child(post.getUserid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
    }
}
