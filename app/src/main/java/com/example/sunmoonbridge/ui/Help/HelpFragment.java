package com.example.sunmoonbridge.ui.Help;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.sunmoonbridge.AddNewPost;
import com.example.sunmoonbridge.Post;
import com.example.sunmoonbridge.PostDetail;
import com.example.sunmoonbridge.R;
import com.example.sunmoonbridge.ui.mypage.MyPostEditActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HelpFragment extends Fragment {

    private Spinner spinner;
    ListView customListView;
    CustomAdapter customAdapter;

    Button btAdd;
    ArrayList<Post> posts = new ArrayList<>();
    ArrayList<Post> postCollection= new ArrayList<>();
    //List<Post> posts=new ArrayList<>();
    DatabaseReference root;
    ArrayList<String> urls=new ArrayList<>();

    EditText searchText;
    Button searchbt;

    private String userid1,nickname;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle extra = this.getArguments();

        if(extra != null) {//조건을 작성하지 않으면 에로 남
            userid1= extra.getString("id");
            nickname= extra.getString("nickname");
        }
        //Toast.makeText(getActivity(),nickname,Toast.LENGTH_SHORT).show();

        View rootView = inflater.inflate(R.layout.main_help_fragment, container, false);

        btAdd = rootView.findViewById(R.id.btadd);
        searchText=rootView.findViewById(R.id.et_search);

        spinner = rootView.findViewById(R.id.to_search_spinner);
        ArrayAdapter<CharSequence> sadapter = ArrayAdapter.createFromResource(getContext(),R.array.spinner_array, android.R.layout.simple_spinner_item);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sadapter);

        root = FirebaseDatabase.getInstance().getReference().child("PostData").child("Help");
        final String postn= "postdetail";//&&postn.equals(ds2)
        final String imgfile = "imagefile";

        // Help에 해당하는 개시물들을 모두 읽어온다.
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String alldata ="";
                posts.clear();
                for(DataSnapshot ds : snapshot.getChildren()){//Help　안에 있는 노드들
                    for(DataSnapshot ds2: ds.getChildren()){//reference postdetail or imagefile node
                        //취특한 이미지 url를 한 문장으로 만듦
                        if (imgfile.equals(ds2.getKey())){

                            for (DataSnapshot ds3:ds2.getChildren()){
                                String url = (String) ds3.getValue();
                                alldata+=url+"***";
                                //urls.add(url);
                            }
                        }

                        if(postn.equals(ds2.getKey())) {//userid getkey로 chat내용인지 개시굴인지 구별인증 ★순서가 맞는지 확인 필요
                            //ds2노드에서 받아 온 데이터를 post객체형대로 받아오기
                            Post post = ds2.getValue(Post.class);
                            //한문장으로 정리한 image URL를 post객체에 저장
                            post.setDlUrlsingle(alldata);
                            //posts(arraylist)에 추가
                            posts.add(post);

                            //오름차순 정렬
                            Collections.sort(posts, new Comparator<Post>() {
                                @Override
                                public int compare(Post post1, Post t1) {
                                    return t1.getPostid().compareTo(post1.getPostid());

                                }
                            });

                            customAdapter.notifyDataSetChanged();
                            alldata = "";
                            urls.clear();

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        customListView = rootView.findViewById(R.id.listView_custom);
        customAdapter = new CustomAdapter(getContext(),postCollection);
        postCollection = posts;
        customListView.setAdapter(customAdapter);
        //customListView.setTextFilterEnabled(true);//★ 必要か検討

        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.class,"selsected "+i,Toast.LENGTH_LONG).show();
                if(userid1.equals(postCollection.get(i).getUserid())){
                    gomydetail(userid1,nickname,postCollection.get(i));
                }else {
                    godetail(userid1,nickname,postCollection.get(i));
                }
            }
        });

        // 개시물 등록기능
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goaddPost(userid1,nickname);
            }
        });

        // 검색기능
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String set = searchText.getText().toString();
                ArrayList<Post> g = new ArrayList<>();
                postCollection = posts;
                if(set.length()==0){
                    postCollection = posts;
                    customAdapter = new CustomAdapter(getContext(),posts);
                    customListView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                }else{
                    for(int i = 0; i < postCollection.size(); i++){
                        if(postCollection.get(i).getNote().contains(set) || postCollection.get(i).getTitle().contains(set)){
                            g.add(postCollection.get(i));
                        }
                    }
                    postCollection = new ArrayList<>(g);
                    customAdapter = new CustomAdapter(getContext(),postCollection);
                    customListView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                }
            }
        });

        // 스피너의 동작
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    //Toast.makeText(getContext(), "touch All", Toast.LENGTH_SHORT).show();
                    postCollection = posts;
                    customAdapter = new CustomAdapter(getContext(),posts);
                    customListView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                }else if (position == 1){
                    //Toast.makeText(getContext(), "touch Give", Toast.LENGTH_SHORT).show();
                    postCollection = posts;
                    ArrayList<Post> g = new ArrayList<>();
                    for(int i=0;i<postCollection.size();i++){
                        if(postCollection.get(i).getDivision().equals("give")){
                            g.add(postCollection.get(i));
                        }
                    }
                    postCollection = new ArrayList<>(g);
                    customAdapter = new CustomAdapter(getContext(),postCollection);
                    customListView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                }else if (position == 2){
                    //Toast.makeText(getContext(), "touch Take", Toast.LENGTH_SHORT).show();
                    postCollection = posts;
                    ArrayList<Post> g = new ArrayList<>();
                    for(int i=0;i<postCollection.size();i++){
                        if(postCollection.get(i).getDivision().equals("take")){
                            g.add(postCollection.get(i));
                        }
                    }
                    postCollection = new ArrayList<>(g);
                    customAdapter = new CustomAdapter(getContext(),postCollection);
                    customListView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                }else if (position == 3){
                    //Toast.makeText(getContext(), "touch My Post", Toast.LENGTH_SHORT).show();
                    postCollection = posts;
                    ArrayList<Post> g = new ArrayList<>();
                    for(int i=0;i<postCollection.size();i++){
                        if(postCollection.get(i).getUserid().equals(userid1)){
                            g.add(postCollection.get(i));
                        }
                    }
                    postCollection = new ArrayList<>(g);
                    customAdapter = new CustomAdapter(getContext(),postCollection);
                    customListView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return rootView;
    }

    // 게시물 등록화면으로 이동하는 메서드
    public void goaddPost(String uid,String name){
        Intent it= new Intent(getActivity(), AddNewPost.class);//fragment는 Context타입이 아니므로, 부모 activity를 호출해서 사용.
        it.putExtra("Userid",uid);
        it.putExtra("nickname",name);
        it.putExtra("type of post","Help");
        startActivity(it);
    }

    // 개시물 상세화면으로 이동하는 에서드

    public void godetail(String uid,String name,Post ps){

        Intent it= new Intent(getActivity(), PostDetail.class);
        it.putExtra("detail",ps);
        it.putExtra("Userid",uid);
        it.putExtra("nickname",name);
        it.putExtra("type of post","Help");
        startActivity(it);
    }

    public void gomydetail(String uid,String name,Post ps){
        Intent it= new Intent(getActivity(), MyPostEditActivity.class);
        it.putExtra("detail",ps);
        it.putExtra("Userid",uid);
        it.putExtra("nickname",name);
        it.putExtra("type of post","Help");
        startActivity(it);
    }

}