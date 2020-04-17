package com.kgy.randomchat.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kgy.randomchat.ChatData;
import com.kgy.randomchat.R;
import com.kgy.randomchat.UserData;
import com.kgy.randomchat.UsersAdapter;

import org.w3c.dom.Comment;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UsersFragment extends Fragment {

    private static final String TAG = "RandomChat";
    private RecyclerView recyclerView;
    private UsersAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private ArrayList<UserData> userDataArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        userDataArrayList = new ArrayList<UserData>();
        database = FirebaseDatabase.getInstance();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserData userData = snapshot.getValue(UserData.class);

                    SharedPreferences sharedPref = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
                    String userEmail = sharedPref.getString("email","");

                    // 로그인한 아이디는 회원 목록에서 제외
                    if (!userEmail.equals(userData.getEmail())){
                        userDataArrayList.add(userData);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        DatabaseReference myRef = database.getReference("userNickName");
        myRef.addValueEventListener(postListener);

        View root = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new UsersAdapter(userDataArrayList);
        recyclerView.setAdapter(mAdapter);

        return root;
    }
}
