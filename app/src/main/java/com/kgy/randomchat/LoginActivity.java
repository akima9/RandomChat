package com.kgy.randomchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "RandomChat";
    private FirebaseAuth mAuth;
    private EditText userEmail;
    private EditText userPw;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        userEmail = (EditText) findViewById(R.id.userEmail);
        userPw = (EditText) findViewById(R.id.userPw);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // 회원가입 버튼
        Button register_btn = findViewById(R.id.register_btn);
        // 로그인 버튼
        Button login_btn = findViewById(R.id.login_btn);

        // 회원가입 버튼 클릭
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 로그인 버튼 클릭
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                String email = userEmail.getText().toString();
                String password = userPw.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Log.d(TAG, "emailVerified: "+user.isEmailVerified());

                                    // 이메일 인증 유무 확인
                                    if (user.isEmailVerified()){
                                        SharedPreferences sharedPref = getSharedPreferences("shared",Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("email",user.getEmail());
                                        editor.commit();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("email",user.getEmail());
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        mAuth.getInstance().signOut();
                                        Toast.makeText(LoginActivity.this, "이메일 인증을 해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }
                            }
                        });
            }
        });


    }
}
