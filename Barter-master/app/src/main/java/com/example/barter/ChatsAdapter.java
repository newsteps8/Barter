package com.example.barter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.barter.models.Message;
import com.example.barter.models.MessageContacts;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Message> messagesArrayList;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private DatabaseReference mDatabase;

    public ChatsAdapter(Activity activity, ArrayList<Message> postArrayList) {

        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messagesArrayList = postArrayList;
    }

    @Override
    public int getCount() {
        return messagesArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.chat_row, null);
        TextView sender_name = (TextView) convertView.findViewById(R.id.sender_names);
        TextView chat_text = (TextView) convertView.findViewById(R.id.chat_text);
        Message post = messagesArrayList.get(position);
        sender_name.setText(post.getSenderName());
        chat_text.setText(post.getMessageText());

        return convertView;
    }
}
