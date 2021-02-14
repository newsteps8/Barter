package com.example.barter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barter.models.MessageContacts;
import com.example.barter.models.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ContactsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<MessageContacts> postArrayList;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private DatabaseReference mDatabase;

    public ContactsAdapter(Activity activity, ArrayList<MessageContacts> postArrayList) {

        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.postArrayList = postArrayList;
    }

    @Override
    public int getCount() {
        return postArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return postArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.message_row, null);
        TextView body = (TextView) convertView.findViewById(R.id.sender_name);
        Button chatButton =(Button) convertView.findViewById(R.id.select_to_chat_button);
        MessageContacts post = postArrayList.get(position);
        body.setText(post.getName().replace(",", "."));

        chatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent to_chat = new Intent(v.getContext(), ChatActivity.class);
                to_chat.putExtra("sender_name",post.getName());
                v.getContext().startActivity(to_chat);


            }
        });










        //https://firebasestorage.googleapis.com/v0/b/barter-d852e.appspot.com/o/images%2F753ee6b9-ffbd-4c85-b579-b12c0c84ac4a?alt=media&token=15b98c83-2c38-46f6-9d43-be55c8b71080
        return convertView;
    }
}
