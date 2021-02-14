package com.example.barter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.barter.models.MessageContacts;
import com.example.barter.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    ListView contectList;
    ListView liste;
    ArrayList<MessageContacts> messages = new ArrayList<>();
    ArrayList<Post> posts = new ArrayList<Post>();
    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;
    ContactsAdapter ContactsAdapter;
    BottomNavigationView bottomNavigationView;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        bottomNavigationView=findViewById(R.id.ButtonmNavMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);


        contectList = (ListView) findViewById(R.id.message_list_view);
        liste = (ListView) findViewById(R.id.message_list_view);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("messages/"+user.getUid());
        //System.out.println("here : messages/"+user.getUid());



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey(); // key here

                    MessageContacts newContact = new MessageContacts(key);
                    messages.add(newContact);
                    ContactsAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });



        ContactsAdapter = new ContactsAdapter(this, messages);
        if (liste != null) {
            liste.setAdapter(ContactsAdapter);
        }





    }

    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod=new

            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){

                        case R.id.feed:
                            Intent intent_4=new Intent(MessageActivity.this,FeedActivity.class);
                            startActivity(intent_4);

                            break;
                        case R.id.profile:
                            Intent intent_1=new Intent(MessageActivity.this,Profile.class);
                            startActivity(intent_1);
                            break;
                        case R.id.new_post:
                            //post oluşturma aktivitesi için
                            Intent intent_2=new Intent(MessageActivity.this,createPostActivity.class);
                            startActivity(intent_2);
                            break;
                        case R.id.message:

                            break;
                        case R.id.signout:
                            FirebaseAuth.getInstance().signOut();//signout
                            Intent intent_3=new Intent(MessageActivity.this,MainActivity.class);
                            startActivity(intent_3);
                            break;



                    }
                    return false;
                }
            };

}