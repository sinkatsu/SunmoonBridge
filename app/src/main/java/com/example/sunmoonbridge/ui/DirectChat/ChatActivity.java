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

        // ????????? UID??? ???????????? UID??? ??????
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

        // 1 ????????? ???????????? ????????? ????????? ???????????? ?????????
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

        // 1??? ????????????

        // ????????? ??????????????? ????????? ?????? ??????
        bt_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //???????????? ????????? ???????????? ??????
                String message = et_text_input.getText().toString();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(getApplicationContext(),"????????? ???????????? ????????? ?????????",Toast.LENGTH_SHORT).show();
                    return;
                }
                // ???????????? ?????? ????????? ??????(True)/??????(False)?????? ????????????.
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
                            // ???????????? ?????? ????????? ????????? ????????? ??????, ????????? ?????? ???????????? ??????
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

        // ????????? ???????????? ????????? ????????? ?????? ??????
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

    // toolbar ??? item??? ??????????????? ?????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // toolbar??? back??? ????????? ??? ??????
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ???????????? ???????????? ?????????
    private void sendMessage(String inputMessage){

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String inputTime = sdf.format(date); // ??????
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
        String inputDate = sdf2.format(date); // ?????? ??????

        Message sendMessage = new Message(myid ,myNickName, inputMessage, inputDate, inputTime,"null");

        // DB??? ??????
        DatabaseReference msgNode = root.child("DirectChat").child(myid).child(targetid).push();
        msgNode.setValue(sendMessage);
        DatabaseReference msgNode2 = root.child("DirectChat").child(targetid).child(myid).push();
        msgNode2.setValue(sendMessage);

        // ???????????? ?????? reset
        et_text_input.setText("");
    }

    // ????????? ??????????????? ???????????? ???????????? ?????? ?????? ??????
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {// ????????? ??????????????? ????????? ?????????
                Uri uri = data.getData();

                //firebase Storage ??? ??????
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
                        // firebase Storage ??? ?????? ?????? ??????

                        Calendar calendar = Calendar.getInstance();
                        String inputDate = calendar.get(Calendar.YEAR)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.DAY_OF_MONTH); // ???,???,???

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                        String inputTime = sdf.format(date); // ??????

                        // Realtime Database??? ??????
                        Message sendMessage = new Message(myid, myNickName, "null", inputDate, inputTime, ChildName);
                        DatabaseReference msgNode = root.child("DirectChat").child(myid).child(targetid).push();
                        msgNode.setValue(sendMessage);
                        DatabaseReference msgNode2 = root.child("DirectChat").child(targetid).child(myid).push();
                        msgNode2.setValue(sendMessage);
                    }
                });

                // ???????????? ?????? ????????? ??????(True)/??????(False)?????? ????????????.
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
                                sendPostToFCM("?????? ???????????? ??????");
                            }else if (targetState.equals("False")){
                                return;
                            }
                        }
                    }
                });

            } else if(resultCode == RESULT_CANCELED) { // ?????? ????????? ???????????? ????????????
                Toast.makeText(this, "?????? ?????? ??????", Toast.LENGTH_LONG).show();
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
                //????????? ?????? ?????????
                Toast.makeText(getApplicationContext(), "????????? ?????? ??????", Toast.LENGTH_SHORT).show();
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

    // ?????? ??????????????? ????????? ?????????.
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAARXcVFyA:APA91bEqa8oHDor1iwVG-sOj5iBNj428ZrBepyZ83bD0Mnt4Qpy2QgtWkgTCyeAuIt3hLEQ5_KszMEZ_2-XkSVyRDSyjx0MD6R2Z7a1vmrgz_ZmLwqXFH0Gq0CUcyhEdQis0_VCaFx-Q";
    private void sendPostToFCM(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // FMC ????????? ?????? start
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", message);
                    notification.put("title", myNickName); // title??? ??????????????? ?????? ?????? ??????.
                    root.put("notification", notification);
                    root.put("to", targetToken);
                    // FMC ????????? ?????? end

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
