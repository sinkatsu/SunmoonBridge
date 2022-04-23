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
                            Toast.makeText(LoginActivity.this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
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

        // 로그인 버튼을 눌렀을 때의 동작
        submit.setOnClickListener(v ->{

            String em = email.getText().toString();
            String pass = password.getText().toString();

            if(TextUtils.isEmpty(em)){
                Toast.makeText(this,"Enter login email",Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(pass)){
                Toast.makeText(this,"Enter login password",Toast.LENGTH_SHORT).show();
            }else {
                // 인증을 시도
                mAuth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 인증을 성공한 경우
                        if(task.isSuccessful()){
                            firebaseAuth = FirebaseAuth.getInstance();
                            firebaseUser = firebaseAuth.getCurrentUser();

                            // 이메일 인증이 되어 있는 경우
                            if(mAuth.getCurrentUser().isEmailVerified()){

                                if (checkBox.isChecked()){
                                    PreferenceManager.setString(getApplicationContext(),"id",email.getText().toString());
                                    PreferenceManager.setString(getApplicationContext(),"ps",password.getText().toString());
                                }

                                //////////////////////////
                                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                                Intent logS = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(logS);
                                finish();
                                //////////////////////////////

                            }else{ // 이메일 인증이 되어 있지 않는 경우

                                // 인증정보를 모두 삭제한다.
                                Toast.makeText(LoginActivity.this,"다시 가입 해주세요",Toast.LENGTH_SHORT).show();

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
                        }else{ // 인증에 실페한 경우
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

        //로그인 기억하기 체크박스 유무에 따른 동작 구현
        checkBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    // 체크박스 체크 되어 있으면 //editText에서 아이디와 암호 가져와 PreferenceManager에 저장한다.
                    PreferenceManager.setString(getApplicationContext(), "id", email.getText().toString());//id 키값으로 저장
                    PreferenceManager.setString(getApplicationContext(), "ps", password.getText().toString());//pw 키값으로 저장
                    PreferenceManager.setBoolean(getApplicationContext(), "check", checkBox.isChecked()); //현재 체크박스 상태 값 저장
                } else { //체크박스가 해제되어있으면
                    PreferenceManager.setString(getApplicationContext(), "id", "");//id 키값으로 저장
                    PreferenceManager.setString(getApplicationContext(), "ps", "");//pw 키값으로 저장
                    PreferenceManager.setBoolean(getApplicationContext(), "check", checkBox.isChecked());//현재 체크박스 상태 값 저장
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
        dialog.setMessage("수신한 이페일로 페스워드를 재설정 해 주세요");
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
        dialog.setMessage("다시 시도 해 주세요");
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}
