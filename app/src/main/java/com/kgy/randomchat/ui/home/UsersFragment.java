package com.kgy.randomchat.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kgy.randomchat.ChatActivity;
import com.kgy.randomchat.OnUsersItemSelectedListener;
import com.kgy.randomchat.R;
import com.kgy.randomchat.UserData;
import com.kgy.randomchat.UsersAdapter;

import java.util.ArrayList;

public class UsersFragment extends Fragment {

    private static final String TAG = "RandomChat";
    private RecyclerView recyclerView;
    UsersAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private ArrayList<UserData> userDataArrayList;
    String userEmail;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        userDataArrayList = new ArrayList<UserData>();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_users,container,false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(ViewGroup rootView){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserData userData = snapshot.getValue(UserData.class);

                    SharedPreferences sharedPref = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
                    userEmail = sharedPref.getString("email","");

//                    userDataArrayList.add(userData);
//                    mAdapter.notifyDataSetChanged();

//                    // 로그인한 아이디는 회원 목록에서 제외
                    if (!userEmail.equals(userData.getEmail())){
                        userDataArrayList.add(userData);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        user = mAuth.getCurrentUser();
        DatabaseReference myRef = database.getReference("users");
//        myRef.addValueEventListener(postListener);
        myRef.addListenerForSingleValueEvent(postListener);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new UsersAdapter(userDataArrayList);
        recyclerView.setAdapter(mAdapter);

        // 회원 클릭 이벤트
        mAdapter.setOnItemClickListener(new OnUsersItemSelectedListener() {
            @Override
            public void onUsersSelected(UsersAdapter.MyViewHolder holder, View view, int position) {

                UserData item = mAdapter.getItem(position);

                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("selectedUserKey",item.getKey());
                intent.putExtra("selectedEmail",item.getEmail());
                intent.putExtra("loginedEmail",userEmail);
                intent.putExtra("nickName",item.getNickName());
                startActivity(intent);
            }
        });
    }
}
