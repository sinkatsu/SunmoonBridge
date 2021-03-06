package com.example.sunmoonbridge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button submit;
    CheckBox checkBox;
    FirebaseAuth mAuth, firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference root;
    String e_mail = "", ps = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.Loginpassword);
        submit = findViewById(R.id.submitButton);
        TextView signUpPage = findViewById(R.id.singUpPage);
        TextView passreset = findViewById(R.id.tv_reset_ps);
        checkBox = findViewById(R.id.checkBox);

        Intent it = getIntent();
        e_mail = it.getStringExtra("Email");
        ps = it.getStringExtra("Password");

        email.setText(e_mail);
        password.setText(ps);

        signUpPage.setOnClickListener(v ->{

            Intent intent = new Intent(LoginActivity.this, SignUp.class);
            startActivity(intent);
            finish();

        });

        passreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = new EditText(getApplicationContext());
                editText.setHint("enter your e-mail");
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                dialog.setTitle("Password Reset");
                dialog.setMessage("Enter your e-mail to reset your password");
                dialog.setView(editText);
                dialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editText.getText().toString().isEmpty()){
                            alert(editText.getText().toString());
                            dialog.dismiss();
                        }else {
                            Toast.makeText(LoginActivity.this, "???????????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.setNegativeButton("cancal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        root = firebaseDatabase.getReference();

        // ????????? ????????? ????????? ?????? ??????
        submit.setOnClickListener(v ->{

            String em = email.getText().toString();
            String pass = password.getText().toString();

            if(TextUtils.isEmpty(em)){
                Toast.makeText(this,"Enter login email",Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(pass)){
                Toast.makeText(this,"Enter login password",Toast.LENGTH_SHORT).show();
            }else {
                // ????????? ??????
                mAuth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // ????????? ????????? ??????
                        if(task.isSuccessful()){
                            firebaseAuth = FirebaseAuth.getInstance();
                            firebaseUser = firebaseAuth.getCurrentUser();

                            // ????????? ????????? ?????? ?????? ??????
                            if(mAuth.getCurrentUser().isEmailVerified()){

                                if (checkBox.isChecked()){
                                    PreferenceManager.setString(getApplicationContext(),"id",email.getText().toString());
                                    PreferenceManager.setString(getApplicationContext(),"ps",password.getText().toString());
                                }

                                //////////////////////////
                                Toast.makeText(LoginActivity.this, "????????? ??????", Toast.LENGTH_SHORT).show();

                                Intent logS = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(logS);
                                finish();
                                //////////////////////////////

                            }else{ // ????????? ????????? ?????? ?????? ?????? ??????

                                // ??????????????? ?????? ????????????.
                                Toast.makeText(LoginActivity.this,"?????? ?????? ????????????",Toast.LENGTH_SHORT).show();

                                firebaseAuth = FirebaseAuth.getInstance();
                                firebaseUser = firebaseAuth.getCurrentUser();
                                String UID = firebaseAuth.getUid();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(UID);
                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DirectChat").child(UID);
                                databaseReference.removeValue();
                                databaseReference1.removeValue();
                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            firebaseAuth.signOut();
                                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                                            ActivityCompat.finishAffinity(LoginActivity.this);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            }
                        }else{ // ????????? ????????? ??????
                            String error = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this,error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        boolean boo = PreferenceManager.getBoolean(getApplicationContext(),"check");
        if (boo){
            email.setText(PreferenceManager.getString(getApplicationContext(),"id"));
            password.setText(PreferenceManager.getString(getApplicationContext(),"ps"));
            checkBox.setChecked(true);
        }

        //????????? ???????????? ???????????? ????????? ?????? ?????? ??????
        checkBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    // ???????????? ?????? ?????? ????????? //editText?????? ???????????? ?????? ????????? PreferenceManager??? ????????????.
                    PreferenceManager.setString(getApplicationContext(), "id", email.getText().toString());//id ???????????? ??????
                    PreferenceManager.setString(getApplicationContext(), "ps", password.getText().toString());//pw ???????????? ??????
                    PreferenceManager.setBoolean(getApplicationContext(), "check", checkBox.isChecked()); //?????? ???????????? ?????? ??? ??????
                } else { //??????????????? ?????????????????????
                    PreferenceManager.setString(getApplicationContext(), "id", "");//id ???????????? ??????
                    PreferenceManager.setString(getApplicationContext(), "ps", "");//pw ???????????? ??????
                    PreferenceManager.setBoolean(getApplicationContext(), "check", checkBox.isChecked());//?????? ???????????? ?????? ??? ??????
                }
            }
        });
    }


    public void alert(String e_mail){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = e_mail;

        auth.sendPasswordResetEmail(emailAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Comp();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Fail();
            }
        });
    }

    public void Comp(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
        //dialog.setCancelable(false);
        dialog.setTitle("Send Complete!");
        dialog.setMessage("????????? ???????????? ??????????????? ????????? ??? ?????????");
        dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void Fail(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
        //dialog.setCancelable(false);
        dialog.setTitle("Send Fail!");
        dialog.setMessage("?????? ?????? ??? ?????????");
        dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}
