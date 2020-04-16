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
    private String lsEmail;
    private FirebaseDatabase database;
    private static final String TAG = "RandomChat";
    private ArrayList<ChatData> chatDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatDataArrayList = new ArrayList<ChatData>();
        database = FirebaseDatabase.getInstance();
        lsEmail = getIntent().getStringExtra("email");
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        inputMsg = (EditText) findViewById(R.id.inputMsg);

        Button sendBtn = findViewById(R.id.sendBtn);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
//        String[] myDataset = {"test1","test2"};
        mAdapter = new ChatAdapter(chatDataArrayList, lsEmail);
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
                chats.put("email", lsEmail);
                chats.put("msg", msg);

                // Write a message to the database
                DatabaseReference myRef = database.getReference("message").child(datetime);

                myRef.setValue(chats);
            }
        });

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                ChatData comment = dataSnapshot.getValue(ChatData.class);

                Log.d(TAG, "comment.getEmail(): "+comment.getEmail());
                Log.d(TAG, "comment.getMsg(): "+comment.getMsg());

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
        DatabaseReference myRef = database.getReference("message");
        myRef.addChildEventListener(childEventListener);
    }
}
