package com.kgy.randomchat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private static final String TAG = "RandomChat";
    private ArrayList<ChatData> mDataset;
    private String selectedEmail;
    String loginedEmail;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        
        public TextView textView;
        public TextView tvNick;
        public MyViewHolder(View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.tvChat);
            tvNick = (TextView)v.findViewById(R.id.tvNick);
        }
    }

    public ChatAdapter(ArrayList<ChatData> myDataset, String lsEmail, String loginedEmail) {
        Log.d(TAG, "ChatAdapter: lsEmail = "+lsEmail);
        mDataset = myDataset;
        this.selectedEmail = lsEmail;
        this.loginedEmail = loginedEmail;
    }

    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
        if (viewType == 1){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.right_row_chat, parent, false);
        }
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // 선택 된 email, 로그인 한 email의 msg만 set
//        if (mDataset.get(position).getEmail().equals(selectedEmail) || mDataset.get(position).getEmail().equals(loginedEmail)){
//            holder.textView.setText(mDataset.get(position).getMsg());
//        }
        holder.textView.setText(mDataset.get(position).getMsg());
        if (holder.tvNick != null){
            holder.tvNick.setText(mDataset.get(position).getResNickName());
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position).getSendUser().equals(loginedEmail)){
            return 1;
        }else{
            return 2;
        }
    }
}
