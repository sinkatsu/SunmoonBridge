package com.example.sunmoonbridge.ui.DirectChat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ChatActivity extends AppCompatActivity implements MessageAdapter.loadImageInterface{

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference root = firebaseDatabase.getReference();

    Intent it;
    String myid, targetid, myNickName, targetToken, targetNickname;

    ListView listView;
    ArrayList<Message> messages;
    MessageAdapter mAdapter;
    Button bt_send_msg, bt_send_img;
    EditText et_text_input;
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room_layout);

        et_text_input = (EditText)findViewById(R.id.et_input);
        bt_send_msg = (Button)findViewById(R.id.bt_send);
        bt_send_img = (Button)findViewById(R.id.bt_img_upload);

        // 자신의 UID와 상대방의 UID를 습득
        it = getIntent();
        myid = it.getStringExtra("MID");
        targetid = it.getStringExtra("TID");
        myNickName = it.getStringExtra("MyNickName");
        targetToken = it.getStringExtra("TToken");
        targetNickname = it.getStringExtra("targetNickname");

        toolbar = findViewById(R.id.chat_room_toolbar);
        toolbar.setTitle(targetNickname);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messages = new ArrayList<Message>();

        // 1 변영된 메시지는 받아서 화면에 반영하는 메서드
        root.child("DirectChat").child(myid).child(targetid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message newMessage = snapshot.getValue(Message.class);
                messages.add(newMessage);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // 1은 여기까지

        // 메시지 송신머튼을 눌렀을 떼의 처리
        bt_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //사용자가 입력한 메시지를 확안
                String message = et_text_input.getText().toString();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(getApplicationContext(),"전송할 메시지를 입력해 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                // 상대방이 알림 수신을 허용(True)/거부(False)인지 확인한다.
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference root = firebaseDatabase.getReference();
                root.child("User").child(targetid).child("profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Error Getting UserData",Toast.LENGTH_SHORT).show();
                        }else{
                            StudentMembars sm = (StudentMembars)task.getResult().getValue(StudentMembars.class);
                            String targetState = sm.getNotiState();
                            // 상대방이 수신 혀용인 경우는 알림도 송신, 거부인 경우 메시지만 송신
                            if (targetState.equals("True")){
                                sendMessage(message);
                                sendPostToFCM(message);
                            }else if (targetState.equals("False")){
                                sendMessage(message);
                            }
                        }
                    }
                });
            }
        });

        // 사진을 전송하는 머튼을 눌렸을 때의 처리
        bt_send_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 1);
            }
        });

        listView = (ListView)findViewById(R.id.listview_chatarea);
        mAdapter = new MessageAdapter(this, messages, myNickName,this, myid);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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

    // 메시지를 전송하는 메서드
    private void sendMessage(String inputMessage){

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String inputTime = sdf.format(date); // 시간
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
        String inputDate = sdf2.format(date); // 날짜 습득

        Message sendMessage = new Message(myid ,myNickName, inputMessage, inputDate, inputTime,"null");

        // DB에 저장
        DatabaseReference msgNode = root.child("DirectChat").child(myid).child(targetid).push();
        msgNode.setValue(sendMessage);
        DatabaseReference msgNode2 = root.child("DirectChat").child(targetid).child(myid).push();
        msgNode2.setValue(sendMessage);

        // 입력하는 장소 reset
        et_text_input.setText("");
    }

    // 사진을 겔러리에서 선택하고 나녀왔을 때의 동작 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {// 사진을 겔러리에서 가지고 왔다면
                Uri uri = data.getData();

                //firebase Storage 에 저장
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference stroageRef = storage.getReference();

                String ChildName = "DirectChat/"+setImgFileName()+uri.getLastPathSegment();
                StorageReference imageRef = stroageRef.child(ChildName);
                UploadTask uploadTask = imageRef.putFile(uri);

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Toast.makeText(ChatActivity.this, "Upload is image: "+progress+"%", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Upload Exception!!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ChatActivity.this, "Upload Complate!!!", Toast.LENGTH_SHORT).show();
                        // firebase Storage 에 저장 완료 하면

                        Calendar calendar = Calendar.getInstance();
                        String inputDate = calendar.get(Calendar.YEAR)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.DAY_OF_MONTH); // 년,월,일

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                        String inputTime = sdf.format(date); // 시간

                        // Realtime Database에 저장
                        Message sendMessage = new Message(myid, myNickName, "null", inputDate, inputTime, ChildName);
                        DatabaseReference msgNode = root.child("DirectChat").child(myid).child(targetid).push();
                        msgNode.setValue(sendMessage);
                        DatabaseReference msgNode2 = root.child("DirectChat").child(targetid).child(myid).push();
                        msgNode2.setValue(sendMessage);
                    }
                });

                // 상대방이 알림 수신을 허용(True)/거부(False)인지 확인한다.
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference root = firebaseDatabase.getReference();
                root.child("User").child(targetid).child("profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Error Getting UserData",Toast.LENGTH_SHORT).show();
                        }else{
                            StudentMembars sm = (StudentMembars)task.getResult().getValue(StudentMembars.class);
                            String targetState = sm.getNotiState();
                            if (targetState.equals("True")){
                                sendPostToFCM("사진 데이터를 수신");
                            }else if (targetState.equals("False")){
                                return;
                            }
                        }
                    }
                });

            } else if(resultCode == RESULT_CANCELED) { // 어떤 사진도 선택하지 않았다면
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void loadImage(String path, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://navigationtempchattest.appspot.com");
        StorageReference pathReference = storageReference.child(path);
        //gs://navigationtempchattest.appspot.com/DirectChat/20210412_11573833324
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //이미지 로드 실패시
                Toast.makeText(getApplicationContext(), "이미지 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String setImgFileName(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat Format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        String id = Format.format(date);
        return id;
    }

    // 보낸 상대방에게 알림을 보낸다.
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAARXcVFyA:APA91bEqa8oHDor1iwVG-sOj5iBNj428ZrBepyZ83bD0Mnt4Qpy2QgtWkgTCyeAuIt3hLEQ5_KszMEZ_2-XkSVyRDSyjx0MD6R2Z7a1vmrgz_ZmLwqXFH0Gq0CUcyhEdQis0_VCaFx-Q";
    private void sendPostToFCM(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // FMC 메시지 생성 start
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", message);
                    notification.put("title", myNickName); // title를 낵내임으로 변경 해야 한다.
                    root.put("notification", notification);
                    root.put("to", targetToken);
                    // FMC 메시지 생성 end

                    URL Url = new URL(FCM_MESSAGE_URL);
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(root.toString().getBytes("utf-8"));
                    os.flush();
                    conn.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
