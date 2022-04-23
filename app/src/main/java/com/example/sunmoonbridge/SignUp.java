package com.example.sunmoonbridge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignUp extends AppCompatActivity {
    private EditText email,pass,conpass,nickname;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference root;
    String token;
    DatabaseReference childRef, childRef2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        root = firebaseDatabase.getReference();

        Button submit = findViewById(R.id.SignUpButton);
        TextView signUp= findViewById(R.id.singInPage);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        progressBar = findViewById(R.id.signUpProgressBar);

        email = findViewById(R.id.signUpEmail);
        pass = findViewById(R.id.signUppassword);
        conpass = findViewById(R.id.signUpConfirmpassword);
        nickname = findViewById(R.id.signUpNickName);


        submit.setOnClickListener(v ->{

            String Email = email.getText().toString();
            String Pass = pass.getText().toString();
            String ConPass = conpass.getText().toString();
            String Nickname = nickname.getText().toString();

            if(TextUtils.isEmpty(Email)){
                Toast.makeText(this,"Enter the email address",Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(Pass)) {
                Toast.makeText(this, "Enter the password", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(ConPass)) {
                Toast.makeText(this, "Enter the same password", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(Nickname)) {
                Toast.makeText(this, "Enter User Nick Name", Toast.LENGTH_SHORT).show();
            }else if (!TextUtils.equals(Pass,ConPass)) {
                Toast.makeText(this, "The password and confirm password does't meatch", Toast.LENGTH_SHORT).show();
            }else {

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        StudentMembars sm = new StudentMembars(Email,"UserIcon/DefultIcon.png", Nickname, "No Data", "No Data",
                                                "No Data", "No Data", "No Data", "No Token","True",0);
                                        root.child("User").child(mAuth.getUid()).child("profile").setValue(sm);
                                        Toast.makeText(SignUp.this,"인증 메일을 전송하였으니 이메일을 확인하십시오.",Toast.LENGTH_SHORT).show();

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
                                        dialog.setCancelable(false);
                                        dialog.setTitle("인증Email 보냈습니다.");
                                        dialog.setMessage("입력한 Email에 인증 메일을 보냈습니다.확인한 후 로그인해주세요."+
                                                "인증 메일이 도착하지 않으면 로그인버튼을 누른 후 다시 등록해주시기 바랍니다.");
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                It(Email,Pass);
                                            }
                                        });
                                        AlertDialog alertDialog = dialog.create();
                                        alertDialog.show();

                                    }else {
                                        progressBar.setVisibility(View.GONE);
                                        String error = task.getException().getMessage();
                                        Toast.makeText(SignUp.this,error,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            progressBar.setVisibility(View.GONE);
                            String error = task.getException().getMessage();
                            Toast.makeText(SignUp.this,error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void It(String Email,String Pass){
        Intent intent = new Intent(this,LoginActivity.class);

        intent.putExtra("Email",Email);
        intent.putExtra("Password",Pass);

        mAuth.signOut();
        startActivity(intent);
        finish();
    }
}
