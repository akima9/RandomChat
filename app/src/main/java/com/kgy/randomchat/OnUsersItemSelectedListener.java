package com.kgy.randomchat;

import android.view.View;

public interface OnUsersItemSelectedListener {
    public void onUsersSelected(UsersAdapter.MyViewHolder holder, View view, int position);
}
