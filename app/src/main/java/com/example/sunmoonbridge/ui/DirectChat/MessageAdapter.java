package com.example.sunmoonbridge.ui.DirectChat;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.sunmoonbridge.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends ArrayAdapter {

    String myID;
    Context context;
    List list;
    String myNickName;
    loadImageInterface loadImageInterface;

    interface loadImageInterface {
        void loadImage (String path, ImageView imageView);
    }

    class ViewHolder {
        public TextView tv_nickname;
        public TextView tv_msg;
        public TextView tv_date;
        public TextView tv_time;
        public ImageView iv_img;
    }

    public MessageAdapter(@NonNull Context context, ArrayList list, String userName, loadImageInterface load, String myID) {
        super(context,0, list);
        this.context = context;
        this.list = list;
        this.myNickName = userName;
        loadImageInterface = load;
        this.myID = myID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Message message = (Message)list.get(position);

        final ViewHolder viewHolder;
        viewHolder = new ViewHolder();

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (message.getUserID().equals(myID)){ //닉네임을 확인하여 자신 또는 다인의 메시지를 구별하여 출력하는 조건문
            // 메시지가 자신의 메시지인 경우
            if(!message.image.equals("null")) {
                // 사진인 경우
                convertView = layoutInflater.inflate(R.layout.my_image_item, parent, false);
                viewHolder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nick);
                viewHolder.iv_img = (ImageView) convertView.findViewById(R.id.iv_imgbox);
                viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                loadImageInterface.loadImage(message.getImage(), viewHolder.iv_img);
                viewHolder.tv_nickname.setText(message.getNickname());
                viewHolder.tv_date.setText(message.getDate());
                viewHolder.tv_time.setText(message.getTime());
            }else {
                convertView = layoutInflater.inflate(R.layout.my_message_item, parent, false);
                viewHolder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nick);
                viewHolder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msgbox);
                viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_nickname.setText(message.getNickname());
                viewHolder.tv_msg.setText(message.getMsg());
                viewHolder.tv_date.setText(message.getDate());
                viewHolder.tv_time.setText(message.getTime());
            }
        }else if (!message.getUserID().equals(myID)){
            if (!message.image.equals("null")) {
                convertView = layoutInflater.inflate(R.layout.other_image_item, parent, false);
                viewHolder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nick);
                viewHolder.iv_img = (ImageView) convertView.findViewById(R.id.iv_imgbox);
                viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                loadImageInterface.loadImage(message.getImage(), viewHolder.iv_img);
                viewHolder.tv_nickname.setText(message.getNickname());
                viewHolder.tv_date.setText(message.getDate());
                viewHolder.tv_time.setText(message.getTime());
            }else{
                convertView = layoutInflater.inflate(R.layout.other_message_item, parent, false);
                viewHolder.tv_nickname = (TextView)convertView.findViewById(R.id.tv_nick);
                viewHolder.tv_msg = (TextView)convertView.findViewById(R.id.tv_msgbox);
                viewHolder.tv_date = (TextView)convertView.findViewById(R.id.tv_date);
                viewHolder.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
                viewHolder.tv_nickname.setText(message.getNickname());
                viewHolder.tv_msg.setText(message.getMsg());
                viewHolder.tv_date.setText(message.getDate());
                viewHolder.tv_time.setText(message.getTime());
            }
        }

        return convertView;
    }
}
