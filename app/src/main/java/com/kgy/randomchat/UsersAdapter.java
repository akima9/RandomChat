package com.kgy.randomchat;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> implements OnUsersItemSelectedListener {
    private ArrayList<UserData> mDataset;
    private static final String TAG = "RandomChat";
    OnUsersItemSelectedListener onUsersItemSelectedListener;

    @Override
    public void onUsersSelected(MyViewHolder holder, View view, int position) {
        if (onUsersItemSelectedListener != null){
            onUsersItemSelectedListener.onUsersSelected(holder, view, position);
        }
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnUsersItemSelectedListener listener) {
        this.onUsersItemSelectedListener = listener ;
    }



    public UserData getItem(int position){
        return mDataset.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView userNickName;
        public TextView gender;
        public LinearLayout userItem;
        public MyViewHolder(View v, final OnUsersItemSelectedListener onUsersItemSelectedListener) {
            super(v);
            userNickName = v.findViewById(R.id.userNickName);
            gender = v.findViewById(R.id.gender);
            userItem = v.findViewById(R.id.userItem);


            // 회원 리스트 클릭 이벤트
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (onUsersItemSelectedListener != null){
                        onUsersItemSelectedListener.onUsersSelected(MyViewHolder.this, view, position);
                    }
                }
            });

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UsersAdapter(ArrayList<UserData> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
//            ...
        MyViewHolder vh = new MyViewHolder(v, this);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.userNickName.setText(mDataset.get(position).getNickName());
        String sex;
        if (mDataset.get(position).getGender().equals("male")){
            sex = "남성";
        }else {
            sex = "여성";
        }
        holder.gender.setText(sex);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
