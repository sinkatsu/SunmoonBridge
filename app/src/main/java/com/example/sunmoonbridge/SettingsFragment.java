package com.example.sunmoonbridge;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.sunmoonbridge.fcmservice.CloudMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor pEditer;

    SwitchPreference switch_lock;
    Preference logout_preference;
    Preference withdrawal_preference;
    Preference pass_change_preference;
    ////
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ////

    Intent it;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        addPreferencesFromResource(R.xml.root_preferencescreen);

        switch_lock = (SwitchPreference)findPreference("switch_notic");
        logout_preference = (Preference)findPreference("logout_preference");
        withdrawal_preference = (Preference)findPreference("withdrawal_preference");
        //pass_change_preference = (Preference)findPreference("change_password_preference");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        pEditer = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("switch_notic",false)){
            switch_lock.setSummary("????????????");
        }else {
            switch_lock.setSummary("????????????");
        }

        switch_lock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (sharedPreferences.getBoolean("switch_notic",false)){
                    switch_lock.setDefaultValue(true);
                    switch_lock.setSummary("????????????");
                    ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
                    // firebase??? ?????? ?????? ????????? ?????????????????? ????????? True??? ??????
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                    root.child("User").child(mAuth.getUid()).child("profile").child("notiState").setValue("True");
                }else {
                    switch_lock.setDefaultValue(false);
                    switch_lock.setSummary("????????????");
                    ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
                    // firebase??? ?????? ?????? ????????? ?????????????????? ?????? False??? ??????
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                    root.child("User").child(mAuth.getUid()).child("profile").child("notiState").setValue("False");
                }
                return false;
            }
        });

        // ???????????? ?????? ??????
        logout_preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                ActivityCompat.finishAffinity(getActivity());
                startActivity(intent);
                return false;
            }
        });

        // ?????? ?????? ?????? ??????
        withdrawal_preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("????????? ???????????????????????");
                dialog.setMessage("??? ????????? ???????????? ??????????????? ????????? ????????? ???????????? ??? ???????????? ?????? ????????? ??? ??? ????????????.");
                dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Realtime Database ??? ?????? ?????? ???????????? ??????
                        String UID = firebaseAuth.getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(UID);
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DirectChat").child(UID);
                        databaseReference.removeValue();
                        databaseReference1.removeValue();

                        //deleteMyPost(UID);

                        // Auth ??? ?????? ?????? ???????????? ??????
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    firebaseAuth.signOut();
                                    ActivityCompat.finishAffinity(getActivity());
                                }else {
                                    Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    firebaseAuth.signOut();
                                    ActivityCompat.finishAffinity(getActivity());
                                }

                            }
                        });

                        firebaseAuth.signOut();
                        ActivityCompat.finishAffinity(getActivity());
                    }
                });
                dialog.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

                // ?????? ?????? ?????? ??? ????????? ??????
                return false;
            }
        });

        // ???????????? ?????? ??????
        /*
        pass_change_preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // ???????????? ?????? ????????? ????????? ??????
                return false;
            }
        });
         */

    }

    // ?????? ????????????????????? ??????????????? ?????? ??????
    // ??????(https://metal00456.tistory.com/10)
    public void checkFirstRun(){
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun",true);
        if(isFirstRun)
        {
            pEditer.putBoolean("isFirstRun",false);
            pEditer.apply();
        }
    }

    public void deleteMyPost(String UID){
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("PostData");
        databaseReference3.child("Help").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    for(DataSnapshot ds2: ds.getChildren()){
                        Post post = ds2.getValue(Post.class);
                        if (post.getUserid().equals(UID)){
                            databaseReference3.child(post.getPostid()).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference3.child("Trade").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    for(DataSnapshot ds2: ds.getChildren()){
                        Post post = ds2.getValue(Post.class);
                        if (post.getUserid().equals(UID)){
                            databaseReference3.child(post.getPostid()).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference3.child("Trade").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    for(DataSnapshot ds2: ds.getChildren()){
                        Post post = ds2.getValue(Post.class);
                        if (post.getUserid().equals(UID)){
                            databaseReference3.child(post.getPostid()).removeValue();
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
