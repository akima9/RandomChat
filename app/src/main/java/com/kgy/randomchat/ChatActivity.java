package com.kgy.randomchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText inputMsg;
    private String selectedEmail;
    String loginedEmail;
    private FirebaseDatabase database;
    private static final String TAG = "RandomChat";
    private ArrayList<ChatData> chatDataArrayList;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    String lsKey;
    String selectedNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();

        chatDataArrayList = new ArrayList<ChatData>();
        database = FirebaseDatabase.getInstance();
        // 선택된 email
        selectedEmail = getIntent().getStringExtra("selectedEmail");
        selectedNickName = getIntent().getStringExtra("nickName");
        lsKey = getIntent().getStringExtra("selectedUserKey");
        // 로그인 한 email
        loginedEmail = getIntent().getStringExtra("loginedEmail");

//        Log.d(TAG, "onCreate: selectedEmail = "+selectedEmail);
//        Log.d(TAG, "onCreate: loginedEmail = "+loginedEmail);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        Button sendBtn = findViewById(R.id.sendBtn);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ChatAdapter(chatDataArrayList, selectedEmail, loginedEmail);
        recyclerView.setAdapter(mAdapter);

        // 보내기 버튼 클릭 이벤트
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = inputMsg.getText().toString();

                // 현재 시간 받아오기
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String datetime = dateformat.format(c.getTime());

                // email 과 msg hashtable 에 담기
                Hashtable<String, String> chats = new Hashtable<String, String>();
                chats.put("sendUser", loginedEmail);
                chats.put("resUser", selectedEmail);
                chats.put("resNickName", selectedNickName);
                chats.put("msg", msg);

                user = mAuth.getCurrentUser();

                // Write a message to the database
//                DatabaseReference myRef = database.getReference("message").child(datetime);
//                DatabaseReference myRef = database.getReference(user.getUid()).child("message").child(datetime);
                DatabaseReference myRef = database.getReference("chats").child(user.getUid()).child(lsKey).child(datetime);
                myRef.setValue(chats);

                myRef = database.getReference("chats").child(lsKey).child(user.getUid()).child(datetime);
                myRef.setValue(chats);
            }
        });

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                // A new comment has been added, add it to the displayed list
                ChatData comment = dataSnapshot.getValue(ChatData.class);

                chatDataArrayList.add(comment);
                mAdapter.notifyDataSetChanged();

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                ChatData newComment = dataSnapshot.getValue(ChatData.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                ChatData movedComment = dataSnapshot.getValue(ChatData.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(ChatActivity.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
//        DatabaseReference myRef = database.getReference("message");
        user = mAuth.getCurrentUser();
//        DatabaseReference myRef = database.getReference("users").child(lsKey).child("message");
        DatabaseReference myRef = database.getReference("chats").child(user.getUid()).child(lsKey);
        myRef.addChildEventListener(childEventListener);
    }
}
