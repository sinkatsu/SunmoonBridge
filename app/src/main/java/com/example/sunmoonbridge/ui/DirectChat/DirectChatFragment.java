package com.example.sunmoonbridge.ui.DirectChat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sunmoonbridge.R;
import com.example.sunmoonbridge.StudentMembars;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DirectChatFragment extends Fragment {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference root = firebaseDatabase.getReference();
    DatabaseReference childRef = root.child("User");
    DatabaseReference childRef2, childRef3;

    FirebaseAuth firebaseAuth;

    ArrayList<String> targetUID;

    ArrayList<UsersRoom> Users;
    StudentMembars sm, targetsm;
    ListView listView;
    UsersRoomAdapter uAdapter;
    TextView textView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_direct_chat_fragment, container, false);

        // 유저 구별을 위한 정보 습득
        firebaseAuth = FirebaseAuth.getInstance();
        String UID = firebaseAuth.getUid();
        childRef2 = childRef.child(UID);

        Users = new ArrayList<UsersRoom>();
        targetUID = new ArrayList<>();

        // Direct Chat 노드에서 체팅한 이력이 있으면 Chatroom list로서 출력
        root.child("DirectChat").child(UID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                targetUID.add(snapshot.getKey());
                /*
                if (!snapshot.getKey().equals(UID)) {
                    UsersRoom ur = new UsersRoom(snapshot.getKey(),snapshot.getValue(StudentMembars.class).getNickname(),snapshot.getValue(StudentMembars.class).getMyToken());
                    Users.add(ur);
                    uAdapter.notifyDataSetChanged();
                } else if (snapshot.getKey().equals(UID)){
                    sm = snapshot.getValue(StudentMembars.class);
                }
                 */
                // 1명이라도 체팅한 사람이 있으면 TextView를 안보이게 한다.
                if (targetUID.size() > 0){
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                }
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

        // 자신의 User 정보를 습득
        childRef.child(UID).child("profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sm = (StudentMembars)task.getResult().getValue(StudentMembars.class);
            }
        });

        String profile = "profile";

        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users.clear();
                for (DataSnapshot sp : snapshot.getChildren()){
                    for(String targetNode: targetUID)
                    if(sp.getKey().equals(targetNode)){
                        for (DataSnapshot dataSnapshot: sp.getChildren()){
                            if(profile.equals(dataSnapshot.getKey())){
                                targetsm = (StudentMembars) dataSnapshot.getValue(StudentMembars.class);
                                UsersRoom ur = new UsersRoom(sp.getKey(), targetsm.getNickname(), targetsm.getMyToken());
                                Users.add(ur);
                                uAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView = (ListView)rootView.findViewById(R.id.listview_usersroom);
        uAdapter = new UsersRoomAdapter(getContext(),Users);
        listView.setAdapter(uAdapter);
        uAdapter.notifyDataSetChanged();

        textView = (TextView)rootView.findViewById(R.id.room_list_view_text);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(getContext(), Users.get(position).getUsersRoom(), Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getActivity(), ChatActivity.class);
                it.putExtra("MID",UID);
                it.putExtra("TID",Users.get(position).getUsersRoom());
                it.putExtra("targetNickname",Users.get(position).getTargetNickname());
                it.putExtra("TToken",Users.get(position).getTargetToken());
                it.putExtra("MyNickName",sm.getNickname());
                startActivity(it);
            }
        });
         // 상대방 페이지로 디동하는 엑션
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getContext(), Users.get(position).getUsersRoom(), Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getActivity(), OtherPageActivity.class);
                it.putExtra("TID",Users.get(position).getUsersRoom());
                startActivity(it);
            }
        });
         */

        return rootView;
    }

}