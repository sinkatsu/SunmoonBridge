package com.example.sunmoonbridge.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunmoonbridge.Post;
import com.example.sunmoonbridge.PostDetail;
import com.example.sunmoonbridge.R;
import com.example.sunmoonbridge.StudentMembars;
import com.example.sunmoonbridge.ui.Help.CustomAdapter;
import com.example.sunmoonbridge.ui.mypage.MyPostEditActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FavoritesViewActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView listView;
    CustomAdapterFavo customAdapter;
    ArrayList<String> favoritePosts;
    ArrayList<Post> posts;
    ArrayList<String> urls;
    ArrayList<String> types;
    ArrayList<Post> postCollection;

    FirebaseDatabase firebaseDatabase;
    String UID;
    String UNN;
    String userid1,nickname;
    StudentMembars userprofile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_view_activity);

        Intent it = getIntent();
        userid1 = it.getStringExtra("id");
        nickname = it.getStringExtra("nickname");


    }

    @Override
    protected void onStart() {
        super.onStart();

        // ????????? ???????????? ???????????? ?????? ???????????? ????????? ID ????????? ??????
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UID = firebaseAuth.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference serchTragetNode = firebaseDatabase.getReference();
        serchTragetNode.child("User").child(UID).child("profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Error Getting UserData",Toast.LENGTH_SHORT).show();
                }else{
                    userprofile = (StudentMembars)task.getResult().getValue(StudentMembars.class);
                    UNN = userprofile.getNickname();
                }
            }
        });

        toolbar = findViewById(R.id.favorite_view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.favorites_listview);

        favoritePosts = new ArrayList<>();
        posts = new ArrayList<>();
        types = new ArrayList<>();
        urls = new ArrayList<>();
        postCollection = new ArrayList<>();

        final String postn= "postdetail";
        final String imgfile = "imagefile";

        // ?????????????????? ????????? ID??? ????????????.
        serchTragetNode.child("User").child(UID).child("favorite").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    favoritePosts.clear();
                    postCollection.clear();
                    types.clear();
                    urls.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        favoritePosts.add(ds.getKey());
                    }
                    posts.clear();
                    // ??????????????? ????????? ???????????? ???????????? ??????
                    serchTragetNode.child("PostData").child("Help").addValueEventListener(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String alldata ="";
                            for(DataSnapshot ds : snapshot.getChildren()){//Help????????? ?????? ?????????
                                for (String fav: favoritePosts) { //
                                    if (fav.equals(ds.getKey())) { // ??????????????? ?????? ???????????????
                                        for (DataSnapshot ds2 : ds.getChildren()) {//reference postdetail or imagefile node
                                            if (imgfile.equals(ds2.getKey())) {
                                                for (DataSnapshot ds3 : ds2.getChildren()) {
                                                    String url = (String) ds3.getValue();
                                                    alldata += url + "***";
                                                    //urls.add(url);
                                                }
                                            }

                                            if (postn.equals(ds2.getKey())) {//userid getkey??? chat???????????? ??????????????? ???????????? ???????????? ????????? ?????? ??????
                                                //ds2???????????? ?????? ??? ???????????? post??????????????? ????????????
                                                Post post = ds2.getValue(Post.class);
                                                //??????????????? ????????? image URL??? post????????? ??????
                                                post.setDlUrlsingle(alldata);
                                                //posts(arraylist)??? ??????
                                                posts.add(post);
                                                types.add("Help");

                                                customAdapter.notifyDataSetChanged();
                                                alldata = "";
                                                urls.clear();

                                            }
                                        }
                                    }else {
                                        //serchTragetNode.child("User").child(UID).child("favorite").child(ds.getKey()).removeValue();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    serchTragetNode.child("PostData").child("Trade").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String alldata ="";
                            for(DataSnapshot ds : snapshot.getChildren()){//Help????????? ?????? ?????????
                                for (String fav: favoritePosts) { //
                                    if (fav.equals(ds.getKey())) { // ??????????????? ?????? ???????????????
                                        for (DataSnapshot ds2 : ds.getChildren()) {//reference postdetail or imagefile node

                                            if (imgfile.equals(ds2.getKey())) {
                                                for (DataSnapshot ds3 : ds2.getChildren()) {
                                                    String url = (String) ds3.getValue();
                                                    alldata += url + "***";
                                                    //urls.add(url);
                                                }
                                            }

                                            if (postn.equals(ds2.getKey())) {//userid getkey??? chat???????????? ??????????????? ???????????? ???????????? ????????? ?????? ??????
                                                //ds2???????????? ?????? ??? ???????????? post??????????????? ????????????
                                                Post post = ds2.getValue(Post.class);
                                                //??????????????? ????????? image URL??? post????????? ??????
                                                post.setDlUrlsingle(alldata);
                                                //posts(arraylist)??? ??????
                                                posts.add(post);
                                                types.add("Trade");

                                                customAdapter.notifyDataSetChanged();
                                                alldata = "";
                                                urls.clear();

                                            }
                                        }
                                    }else {
                                        //serchTragetNode.child("User").child(UID).child("favorite").child(ds.getKey()).removeValue();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    serchTragetNode.child("PostData").child("Knowledge").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String alldata ="";
                            for(DataSnapshot ds : snapshot.getChildren()){//Help????????? ?????? ?????????
                                for (String fav: favoritePosts) { //
                                    if (fav.equals(ds.getKey())) { // ??????????????? ?????? ???????????????
                                        for (DataSnapshot ds2 : ds.getChildren()) {//reference postdetail or imagefile node

                                            if (imgfile.equals(ds2.getKey())) {
                                                for (DataSnapshot ds3 : ds2.getChildren()) {
                                                    String url = (String) ds3.getValue();
                                                    alldata += url + "***";
                                                    //urls.add(url);
                                                }
                                            }

                                            if (postn.equals(ds2.getKey())) {//userid getkey??? chat???????????? ??????????????? ???????????? ???????????? ????????? ?????? ??????
                                                //ds2???????????? ?????? ??? ???????????? post??????????????? ????????????
                                                Post post = ds2.getValue(Post.class);
                                                //??????????????? ????????? image URL??? post????????? ??????
                                                post.setDlUrlsingle(alldata);
                                                //posts(arraylist)??? ??????
                                                posts.add(post);
                                                types.add("Knowledge");

                                                customAdapter.notifyDataSetChanged();
                                                alldata = "";
                                                urls.clear();

                                            }
                                        }
                                    }else {
                                        //serchTragetNode.child("User").child(UID).child("favorite").child(ds.getKey()).removeValue();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });

        postCollection = posts;
        posts.clear();
        customAdapter = new CustomAdapterFavo(getApplicationContext(),postCollection, types);
        listView.setAdapter(customAdapter);

        // ???????????? ??????????????? ?????? ?????? ??????
        // ????????? ?????? ?????? ??????
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(userid1.equals(postCollection.get(i).getUserid())){
                    gomydetail(userid1,nickname,postCollection.get(i),types.get(i));
                }else {
                    godetail(userid1,nickname,postCollection.get(i),types.get(i));
                }
            }
        });

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

    // ????????? ?????????????????? ???????????? ?????????
    public void godetail(String uid,String name,Post ps, String type){

        Intent it= new Intent(getApplicationContext(), PostDetail.class);
        it.putExtra("detail",ps);
        it.putExtra("Userid",uid);
        it.putExtra("nickname",name);
        it.putExtra("type of post",type);
        startActivity(it);
    }

    public void gomydetail(String uid,String name,Post ps, String type){
        Intent it= new Intent(getApplicationContext(), MyPostEditActivity.class);
        it.putExtra("detail",ps);
        it.putExtra("Userid",uid);
        it.putExtra("nickname",name);
        it.putExtra("type of post",type);
        startActivity(it);
    }
}
