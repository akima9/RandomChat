package com.kgy.randomchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Hashtable;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RandomChat";
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText userEmail;
    private EditText userPw1;
    private EditText userPw2;
    private EditText userNickName;
    private RadioGroup genderRadioGroup;
    private RadioButton genderMaleBtn;
    private RadioButton genderFemaleBtn;
    private ProgressBar progressBar;
    private String nickName;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = (EditText) findViewById(R.id.userEmail);
        userPw1 = (EditText) findViewById(R.id.userPw1);
        userPw2 = (EditText) findViewById(R.id.userPw2);
        userNickName = (EditText) findViewById(R.id.userNickName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        genderMaleBtn = (RadioButton) findViewById(R.id.genderMaleBtn);
        genderFemaleBtn = (RadioButton) findViewById(R.id.genderFemaleBtn);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 회원가입 버튼
        Button register_btn = (Button) findViewById(R.id.register_btn);

        // 회원가입 버튼 클릭 이벤트
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = userEmail.getText().toString();
                String password1 = userPw1.getText().toString();
                String password2 = userPw2.getText().toString();
                nickName = userNickName.getText().toString();

                database = FirebaseDatabase.getInstance();

                // 이메일 입력 안했을 경우
                if (email.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 닉네임 입력 안했을 경우
                if (nickName.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 비밀번호 입력 안했을 경우
                if (password1.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 비밀번호 재입력 안했을 경우
                if (password2.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "비밀번호 재입력을 해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (gender.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 비밀번호 != 비밀번호 재입력 일 경우
                if (!password1.isEmpty() && !password2.isEmpty()){
                    if (!password1.equals(password2)){
                        Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                progressBar.setVisibility(View.VISIBLE); // 프로그레스바 노출

                mAuth.createUserWithEmailAndPassword(email, password1)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    user = mAuth.getCurrentUser();

                                    //해당기기의 언어 설정
                                    mAuth.setLanguageCode("ko");

                                    // 인증 메일 보내기
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                                Toast.makeText(RegisterActivity.this,
                                                        "인증 메일을 확인해주세요. (" + user.getEmail()+")",
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Log.e(TAG, "sendEmailVerification", task.getException());
                                                Toast.makeText(RegisterActivity.this,
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    String email = user.getEmail().replace(".","_");

                                    // hashtable 에 담기
                                    Hashtable<String, String> users = new Hashtable<String, String>();
                                    users.put("email", user.getEmail());
                                    users.put("nickName", nickName);
                                    users.put("gender", gender);

                                    // Write a message to the database
                                    DatabaseReference myRef = database.getReference("userNickName").child(email);
                                    myRef.setValue(users);

                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    public void onRadioButtonClicked(View view) {
        
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.genderMaleBtn:
                if (checked) {
                    gender = "male";
                    break;
                }
            case R.id.genderFemaleBtn:
                if (checked) {
                    gender = "Female";
                    break;
                }
        }
    }
}
