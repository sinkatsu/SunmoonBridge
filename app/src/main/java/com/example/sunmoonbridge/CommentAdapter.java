package com.example.sunmoonbridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends ArrayAdapter {

    Context context;
    List list;

    class ViewHolder {
        public TextView tv_nickname;
        public TextView tv_msg;
        public TextView tv_date;
    }

    public CommentAdapter(@NonNull Context context, ArrayList list){
        super(context,0, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Comment comment = (Comment)list.get(position);

        final ViewHolder viewHolder;
        viewHolder = new ViewHolder();

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        convertView = layoutInflater.inflate(R.layout.comment_item, parent, false);
        viewHolder.tv_nickname = (TextView) convertView.findViewById(R.id.comment_tv_name);
        viewHolder.tv_date = (TextView)convertView.findViewById(R.id.comment_tv_date);
        viewHolder.tv_msg = (TextView)convertView.findViewById(R.id.comment_tv_msg);
        viewHolder.tv_nickname.setText(comment.getCommentNickname());
        viewHolder.tv_date.setText(comment.getCommentDate());
        viewHolder.tv_msg.setText(comment.getCommentMsg());

        return convertView;
    }
}
