package com.example.barter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barter.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {


    ListView liste;
    ArrayList<Post> posts = new ArrayList<Post>();
    postsAdapter postsAdapter ;
    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;
    BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        bottomNavigationView=findViewById(R.id.ButtonmNavMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);



        liste = (ListView) findViewById(R.id.listView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey(); // key here
                    String author = (String)snapshot.child("author").getValue();
                    String body = (String)snapshot.child("body").getValue();
                    String price = (String)snapshot.child("price").getValue();
                    String uid = (String)snapshot.child("uid").getValue();
                    String iamge_uri = (String)snapshot.child("image_uri").getValue();
                    double userLat = (double)snapshot.child("latitude").getValue();
                    double userLong = (double)snapshot.child("longitude").getValue();

                    Post newPost = new Post(uid,author,body,price,userLat,userLong,iamge_uri);
                    posts.add(newPost);
                    postsAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });
        postsAdapter = new postsAdapter(this, posts);
        if (liste != null) {
            liste.setAdapter(postsAdapter);
        }


        System.out.println("here we go");
        //System.out.println("liste is : : " + liste);

        liste.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView) view.findViewById(R.id.body);
                String text = textView.getText().toString();
                System.out.println("Choosen body = : " + text);

            }
        });




    }
    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod=new

            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){

                        case R.id.feed:
                            break;
                        case R.id.profile:
                            Intent intent_1=new Intent(FeedActivity.this,Profile.class);
                            startActivity(intent_1);
                            break;
                        case R.id.new_post:
                            //post oluşturma aktivitesi için
                            Intent intent_2=new Intent(FeedActivity.this,createPostActivity.class);
                            startActivity(intent_2);
                            break;
                        case R.id.signout:
                            FirebaseAuth.getInstance().signOut();//signout
                            Intent intent_3=new Intent(FeedActivity.this,MainActivity.class);
                            startActivity(intent_3);
                            break;
                        case R.id.message:
                            //post oluşturma aktivitesi için
                            Intent intent_4=new Intent(FeedActivity.this,MessageActivity.class);
                            startActivity(intent_4);
                            break;



                    }
                    return false;
                }
            };


}