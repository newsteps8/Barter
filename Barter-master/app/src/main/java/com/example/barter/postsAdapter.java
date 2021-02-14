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

import com.bumptech.glide.Glide;
import com.example.barter.models.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class postsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Post> postArrayList;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private DatabaseReference mDatabase;

    public postsAdapter(Activity activity, ArrayList<Post> postArrayList) {

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

        convertView = mInflater.inflate(R.layout.list_row, null);
        TextView body = (TextView) convertView.findViewById(R.id.body);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        TextView author = (TextView) convertView.findViewById(R.id.author);
        ImageView postImage = (ImageView) convertView.findViewById(R.id.imageView);
        Button mapsButton =(Button) convertView.findViewById(R.id.mapsButton);
        Post post = postArrayList.get(position);
        body.setText(post.getBody());
        price.setText(post.getPrice());
        author.setText(post.getAuthor());
        Button chatOwnerButton =(Button) convertView.findViewById(R.id.chatOwner);


        if(post.getImageUrl() != null){
            StorageReference imageRef = storageRef.child("images/"+post.getImageUrl());
            //System.out.println("image url is : " + post.getImageUrl());

            GlideApp.with(parent)
                    .load(imageRef)
                    .placeholder(R.drawable.placeholders)
                    .into(postImage);


        }else{
            System.out.println("No image");
        }



        mapsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println(" price is : "+post.getPrice());

                Intent gecisYap = new Intent(v.getContext(), MapsActivity.class);
                gecisYap.putExtra("1",post.getLatitude());
                gecisYap.putExtra("2",post.getLongitude());
                v.getContext().startActivity(gecisYap);


            }
        });

        chatOwnerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent toChat = new Intent(v.getContext(), sendMessageActivity.class);
                toChat.putExtra("uid",post.getUid());
                toChat.putExtra("receiver_mail",post.getAuthor());
                v.getContext().startActivity(toChat);


            }
        });





        //https://firebasestorage.googleapis.com/v0/b/barter-d852e.appspot.com/o/images%2F753ee6b9-ffbd-4c85-b579-b12c0c84ac4a?alt=media&token=15b98c83-2c38-46f6-9d43-be55c8b71080
        return convertView;
    }
}
