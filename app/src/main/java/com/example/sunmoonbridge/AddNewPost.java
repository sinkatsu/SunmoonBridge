package com.example.sunmoonbridge;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddNewPost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "AddPost";

    TextView a, b, c;//addNewpost layout의 textview
    EditText etnote, title;
    ImageButton imageButton;
    ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    SwitchCompat switchCompat;
    Toolbar toolbar;

    int imagecount = 0;
    int totalimage = 0;
    boolean imageloadcheck=false;
    private int upload_count = 0;
    private Uri filePath;
    ArrayList<StudentMembars> users = new ArrayList<>();
    private int postcount;
    String nodename;
    String uritest;
    Intent it;
    String dateend;
    String division;
    Spinner spinnerDivision;
    boolean firstcall = true;
    String[] divisionlist = {"take", "give"};
    private ArrayList<Uri> filePaths = new ArrayList<>();
    private ArrayList<String> downloadUrls = new ArrayList<>();
    String userid, nickname, posttype;
    Post newPost;

    DatabaseReference dbrefHelp, dbrefTrade, dbrefKnow, userref, manageref;
    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_add_activity);

        it = getIntent();
        userid = it.getStringExtra("Userid");
        nickname = it.getStringExtra("nickname");
        posttype = it.getStringExtra("type of post");

        //Toast.makeText(getApplicationContext(), userid + nickname, Toast.LENGTH_LONG).show();

        //초기화
        storageReference = FirebaseStorage.getInstance().getReference("post images");
        dbrefHelp = FirebaseDatabase.getInstance().getReference("PostData").child("Help");
        dbrefTrade = FirebaseDatabase.getInstance().getReference("PostData").child("Trade");
        dbrefKnow = FirebaseDatabase.getInstance().getReference("PostData").child("Knowledge");
        userref = FirebaseDatabase.getInstance().getReference("User");
        manageref = FirebaseDatabase.getInstance().getReference("Manager").child("postmanagement").child("postnum");

        a = findViewById(R.id.textView9);
        b = findViewById(R.id.textviewtitle);
        c = findViewById(R.id.textviewnote);
        etnote = findViewById(R.id.et_note);
        progressBar = findViewById(R.id.progress_bar);
        title = findViewById(R.id.et_title01);
        CalendarView calendarView = findViewById(R.id.calendar01);
        calendarView.setVisibility(View.GONE);
        calendarView.setMinDate((new Date().getTime()));

        toolbar = findViewById(R.id.edit_post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Create New Post");

        //마감날 받기
        Calendar calendarEnd = new GregorianCalendar();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                calendarEnd.set(i, i1, i2);

                //Log.d(TAG,"selected:mm/dd/yyyy:"+d);
                //dateend=calendarEnd.get(Calendar.YEAR)+"."+(calendarEnd.get(Calendar.MONTH)+1)+"."+calendarEnd.get(Calendar.DAY_OF_MONTH);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                dateend = sdf.format(calendarEnd.getTime());
                Toast.makeText(getApplicationContext(), sdf.format(calendarEnd.getTime()), Toast.LENGTH_SHORT).show();
            }
        });


        switchCompat = (SwitchCompat) findViewById(R.id.switch1);
        spinnerDivision = (Spinner) findViewById(R.id.spinner1);
        //마감 설정
        if (posttype.equals("knowledge")) {
            a.setVisibility(View.GONE);
            c.setText("질문 내용");
            switchCompat.setVisibility(View.GONE);
            spinnerDivision.setVisibility(View.GONE);
        } else {

            switchCompat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (switchCompat.isChecked()) {
                        calendarView.setVisibility(View.VISIBLE);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                        dateend = sdf.format(calendarEnd.getTime());
                        //Toast.makeText(getApplicationContext(),"chip is "+dateend,Toast.LENGTH_SHORT).show();
                    } else {
                        calendarView.setVisibility(View.GONE);
                        dateend = null;
                    }
                }
            });


            ArrayAdapter<String> adapter;
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, divisionlist);
            spinnerDivision.setAdapter(adapter);
            spinnerDivision.setSelection(0);
            spinnerDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    division = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }


        imageButton = findViewById(R.id.imageButton2);
        image1 = findViewById(R.id.imageView);
        image2 = findViewById(R.id.imageView2);
        image3 = findViewById(R.id.imageView3);
        image4 = findViewById(R.id.imageView4);
        image5 = findViewById(R.id.imageView5);
        image6 = findViewById(R.id.imageView6);
        image7 = findViewById(R.id.imageView7);
        image8 = findViewById(R.id.imageView8);
        image9 = findViewById(R.id.imageView9);
        image10 = findViewById(R.id.imageView10);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("post Uploading please wait....");

        //post상세 내용에 필요한 대이터 가져오기
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) { // user id
                    for (DataSnapshot ds2 : ds.getChildren()) { //
                        for (DataSnapshot ds3 : ds2.getChildren()) {
                            if (nickname.equals(ds3.getValue())) {
                                nodename = ds.getKey();//////////////어디를 호출하고 있는지 불명확
                                StudentMembars user = ds2.getValue(StudentMembars.class);////////////
                                postcount = user.getPostCount();
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //각 이미지에 touch처리 를 타단
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 1;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 2;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 3;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 4;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 5;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });
        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 6;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });
        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 7;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });
        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 8;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });
        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 9;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });
        image10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagecount = 10;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
            }
        });



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalimage < 10) {
                    imageloadcheck=true;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), PICK_IMAGE_REQUEST);
                } else
                    Toast.makeText(getApplicationContext(), "더 이상 등록하지 못합니다", Toast.LENGTH_LONG).show();
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
            case R.id.saveEditDataMenu:
                // 게시물 등록 처리 엑션 리스너
                item.setEnabled(false);
                goPostFirst(userid, nickname,item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 메뉴 활성화
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prifile_edit_menu, menu);
        return true;
    }

    //이미지를 내부메모리에서 가져오기 및 filepath 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            if(imageloadcheck==true){
                totalimage++;
                imagecount = totalimage;
                imageloadcheck=false;
            }
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                int imageWidth = bitmap1.getWidth();
                int imageHeight = bitmap1.getHeight();

                int startX = 0;
                int startY = 0;
                if (bitmap1.getWidth() >= bitmap1.getHeight()) {
                    imageWidth = bitmap1.getWidth();
                    imageHeight = bitmap1.getHeight();
                    startX = (imageWidth / 2) - (imageHeight / 2);
                    startY = 0;
                    imageWidth = imageHeight;
                } else if (bitmap1.getWidth() < bitmap1.getHeight()) {
                    imageWidth = bitmap1.getWidth();
                    imageHeight = bitmap1.getHeight();
                    startX = 0;
                    startY = (imageHeight / 2) - (imageWidth / 2);
                    imageHeight = imageWidth;
                }

                Bitmap bitmap = Bitmap.createBitmap(bitmap1, startX, startY, imageWidth, imageHeight);

                //이부분을 함수로 묶을까 고민
                if (imagecount == 1) {
                    image1.setImageBitmap(bitmap);
                    if(filePaths.size()>=1){
                        filePaths.set(0,filePath);
                    }else filePaths.add(0, filePath);//해당 arraylist에 추가함
                } else if (imagecount == 2) {
                    image2.setImageBitmap(bitmap);
                    if(filePaths.size()>=2){
                        filePaths.set(1,filePath);
                    }else filePaths.add(1, filePath);
                } else if (imagecount == 3) {
                    image3.setImageBitmap(bitmap);
                    if(filePaths.size()>=3){
                        filePaths.set(2,filePath);
                    }else filePaths.add(2, filePath);
                } else if (imagecount == 4) {
                    image4.setImageBitmap(bitmap);
                    if(filePaths.size()>=4){
                        filePaths.set(3,filePath);
                    }else filePaths.add(3, filePath);
                } else if (imagecount == 5) {
                    image5.setImageBitmap(bitmap);
                    if(filePaths.size()>=5){
                        filePaths.set(4,filePath);
                    }else filePaths.add(4, filePath);
                } else if (imagecount == 6) {
                    image6.setImageBitmap(bitmap);
                    if(filePaths.size()>=6){
                        filePaths.set(5,filePath);
                    }else filePaths.add(5, filePath);
                } else if (imagecount == 7) {
                    image7.setImageBitmap(bitmap);
                    if(filePaths.size()>=7){
                        filePaths.set(6,filePath);
                    }else filePaths.add(6, filePath);
                } else if (imagecount == 8) {
                    image8.setImageBitmap(bitmap);
                    if(filePaths.size()>=8){
                        filePaths.set(7,filePath);
                    }else filePaths.add(7, filePath);
                } else if (imagecount == 9) {
                    image9.setImageBitmap(bitmap);
                    if(filePaths.size()>=9){
                        filePaths.set(8,filePath);
                    }else filePaths.add(8, filePath);
                } else if (imagecount == 10) {
                    image10.setImageBitmap(bitmap);
                    if(filePaths.size()>=10){
                        filePaths.set(9,filePath);
                    }else filePaths.add(9, filePath);
                }
                //image10.setImageDrawable(null);

                Toast.makeText(getApplicationContext(), "you selected " + imagecount + " images", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(Uri uri) {//imagefiledata에 해당하는 mimetype(확장자(jpg, png등))를 가져온다.
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();//singleton
        return mime.getExtensionFromMimeType(cR.getType(uri));//gettype에선선  type명을 가져온다
    }

    //strage에 imagefile 저장하기
    public void goPostFirst(final String userid, final String nickname, MenuItem item) {

        if (totalimage != 0) {
            String inputrtitle = title.getText().toString();
            String inputnote = etnote.getText().toString();

            if (inputrtitle.equals("")) {
                Toast.makeText(getApplicationContext(), "제목이 입력되지 않았습니다", Toast.LENGTH_LONG).show();
                item.setEnabled(true);
                return;
            } else if (inputnote.equals("")) {
                Toast.makeText(getApplicationContext(), "내용이 입력되지 않았습니다", Toast.LENGTH_LONG).show();
                item.setEnabled(true);
                return;
            }

            for (upload_count = 0; upload_count < filePaths.size(); upload_count++) {
                Uri IndividualImage = filePaths.get(upload_count);
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                        + ".jpg");//파일명지정
                //final StorageReference fileReference=storageReference.child("image"+IndividualImage.getLastPathSegment());

                fileReference
                        //strage에 파일저장
                        .putFile(IndividualImage)
                        //결과여부
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                progressBar.setProgress(0);

                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uritest = String.valueOf(uri);
                                        downloadUrls.add(String.valueOf(uri));
                                        Toast.makeText(getApplicationContext(), "up image " + upload_count + " and total is " + totalimage, Toast.LENGTH_SHORT).show();
                                        if (downloadUrls != null) {

                                            if (downloadUrls.size() == totalimage) {
                                                goPost(userid, nickname, item);
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(com.example.sunmoonbridge.AddNewPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);

                    }
                });


            }
        }
        else {
            Toast.makeText(getApplicationContext(), "non image", Toast.LENGTH_SHORT).show();
            goPost(userid, nickname, item);
        }
    }

    //RDB에서 노드저장하기
    public void goPost(String userid, String nickname, MenuItem item) {

        //한 유저당 게시물 수
        postcount += 1;
        userref.child(userid).child("profile").child("postCount").setValue(postcount);

        //이미지 upload
        //投稿者の情報はidだけ入れとく？poatidどうする
        //activity에서 내용추출
        String inputrtitle = title.getText().toString();
        String inputnote = etnote.getText().toString();

        if (inputrtitle.equals("")) {
            Toast.makeText(getApplicationContext(), "제목이 입력되지 않았습니다", Toast.LENGTH_LONG).show();
            item.setEnabled(true);
            return;
        } else if (inputnote.equals("")) {
            Toast.makeText(getApplicationContext(), "주석이 입력되지 않았습니다", Toast.LENGTH_LONG).show();
            item.setEnabled(true);
            return;
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String date = sdf.format(calendar.getTime());
        //String pid = userid+postcount;/////////////////
        final DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss:SSS");
        final Date date1 = new Date(System.currentTimeMillis());
        String pid = df.format(date1);

        //firebase에서 ".","$"등의 값을저장하면 그러한 값을 포함하는 노드를 키로 참조하면 에로
        if (posttype.equals("knowledge")) {
            newPost = new Post(pid, inputrtitle, nickname, inputnote, date,"knowledge", totalimage, userid,0);
        } else
            newPost = new Post(pid, inputrtitle, nickname, inputnote, date, dateend, division, totalimage, userid,0);


        //Toast.makeText(getApplicationContext(),"url is  "+inputURL, Toast.LENGTH_LONG).show();

        if (posttype.equals("Help")) {
            DatabaseReference node = dbrefHelp.child(pid).child("postdetail");
            DatabaseReference node2 = node.push();
            node.setValue(newPost);
            //realtime db에 저장함
            if (totalimage != 0) {
                DatabaseReference node3 = dbrefHelp.child(pid).child("imagefile");
                DatabaseReference node4 = node3;
                //node4.setValue("downloadUrls.get(0).toString()");

                for (int i = 0; i < downloadUrls.size(); i++) {
                    node4.push().setValue(downloadUrls.get(i));
                }
            }
        } else if (posttype.equals("Trade")) {
            DatabaseReference node = dbrefTrade.child(pid).child("postdetail");
            DatabaseReference node2 = node.push();
            node.setValue(newPost);
            //realtime db에 저장함
            if (totalimage != 0) {
                DatabaseReference node3 = dbrefTrade.child(pid).child("imagefile");
                DatabaseReference node4 = node3;
                for (int i = 0; i < downloadUrls.size(); i++) {
                    node4.push().setValue(downloadUrls.get(i));
                }
            }
        } else if (posttype.equals("knowledge")) {
            DatabaseReference node = dbrefKnow.child(pid).child("postdetail");
            DatabaseReference node2 = node.push();
            node.setValue(newPost);
            //realtime db에 저장함
            if (totalimage != 0) {
                DatabaseReference node3 = dbrefKnow.child(pid).child("imagefile");
                DatabaseReference node4 = node3;
                for (int i = 0; i < downloadUrls.size(); i++) {
                    node4.push().setValue(downloadUrls.get(i));
                }
            }
        }
        finish();
    }
}
