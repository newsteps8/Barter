package com.example.barter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.barter.models.Message;
import com.example.barter.models.MessageContacts;
import com.example.barter.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    ListView liste;
    ArrayList<Message> messages = new ArrayList<>();
    ArrayList<Post> posts = new ArrayList<Post>();
    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;
    ChatsAdapter ChatsAdapter;

    EditText newMessage;
    Button sendNewMessage;
    String receiverId = "";


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        newMessage = (EditText) findViewById(R.id.newMessage);
        sendNewMessage = (Button) findViewById(R.id.sendNewMessage);

        liste = (ListView) findViewById(R.id.chat_list_view);

        Intent intent = getIntent();
        String sender_name = intent.getStringExtra("sender_name");


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("messages/"+user.getUid()+"/"+sender_name.replace(".", ","));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    String message = (String)snapshot.child("messageText").getValue();// key here
                    String senderName = (String)snapshot.child("senderName").getValue();
                    String temp = (String)snapshot.child("sender").getValue();
                    if (!user.getUid().equals(temp))
                        receiverId =temp;

                    Message newMessage = new Message(message,senderName);
                    messages.add(newMessage);
                    ChatsAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });





        //Message newContact = new Message("g","a","g","a","a");
        //messages.add(newContact);


        ChatsAdapter = new ChatsAdapter(this, messages);
        if (liste != null) {
            liste.setAdapter(ChatsAdapter);
        }


        sendNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = newMessage.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(getApplicationContext(),"Empty message !!",Toast.LENGTH_SHORT).show();
                    return;
                }

                final String userId = user.getUid();
                mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                // [START_EXCLUDE]
                                if (user == null) {

                                    Toast.makeText(ChatActivity.this,
                                            "Error: could not fetch user.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // Write new post
                                    Intent intent = getIntent();
                                    //String receiver_uid = intent.getStringExtra("uid");
                                    String receiver_mail = intent.getStringExtra("sender_name");

                                    String senderName = user.getEmail();


                                    long msTime = System.currentTimeMillis();
                                    Date curDateTime = new Date(msTime);
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd'/'MM'/'y hh:mm");
                                    String dateTime = formatter.format(curDateTime);


                                    sendNewMessage(userId, message,dateTime,receiverId,senderName,receiver_mail);


                                }

                                finish();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                            }
                        });



            }
        });





    }
    private String sendNewMessage(String senderId, String messageText,String time,String receiver, String senderName, String receiver_mail){

        String key = mDatabase.child("messages/"+senderId).push().getKey();
        Message message = new Message(messageText, senderId, time, receiver,senderName);
        Map<String, Object> messageValues = message.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/posts/" + key, messageValues);

        System.out.println("receiver id : " + receiver);
        childUpdates.put("/messages/" + receiver + "/" + senderName.replace(".", ",") +"/"+ key, messageValues);
        childUpdates.put("/messages/" + user.getUid() + "/" + receiver_mail.replace(".", ",") +"/"+ key, messageValues);

        mDatabase.updateChildren(childUpdates);
        finish();
        startActivity(getIntent());
        return key;


    }


    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod=new

            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.profile:
                            Intent intent_5=new Intent(ChatActivity.this,Profile.class);
                            startActivity(intent_5);
                            break;
                        case R.id.feed:
                            Intent intent_1=new Intent(ChatActivity.this,FeedActivity.class);
                            startActivity(intent_1);
                            break;
                        case R.id.new_post:
                            //post oluşturma aktivitesi için
                            Intent intent_2=new Intent(ChatActivity.this,createPostActivity.class);
                            startActivity(intent_2);
                            break;
                        case R.id.signout:
                            FirebaseAuth.getInstance().signOut();//signout
                            Intent intent_3=new Intent(ChatActivity.this,MainActivity.class);
                            startActivity(intent_3);
                            break;
                        case R.id.message:
                            //post oluşturma aktivitesi için
                            Intent intent_4=new Intent(ChatActivity.this,MessageActivity.class);
                            startActivity(intent_4);
                            break;



                    }
                    return false;
                }
            };


}