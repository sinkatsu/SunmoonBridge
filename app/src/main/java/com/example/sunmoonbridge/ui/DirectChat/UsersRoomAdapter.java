package com.example.sunmoonbridge.ui.DirectChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sunmoonbridge.R;

import java.util.ArrayList;
import java.util.List;

public class UsersRoomAdapter extends ArrayAdapter {

    Context context;
    List list;

    class ViewHolder {
        public TextView tv_username;
    }

    public UsersRoomAdapter(@NonNull Context context, ArrayList list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;

        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.chat_room_item, parent, false);
        }

        viewHolder = new ViewHolder();
        viewHolder.tv_username = (TextView)convertView.findViewById(R.id.tv_chatroomname);

        final UsersRoom usersRoom = (UsersRoom)list.get(position);
        viewHolder.tv_username.setText(usersRoom.getTargetNickname());

        return convertView;
    }
}
