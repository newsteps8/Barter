package com.example.barter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.barter.models.Message;
import com.example.barter.models.Post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class sendMessageActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    BottomNavigationView bottomNavigationView;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // ...
    EditText message_to,message_body;
    Button send_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        bottomNavigationView=findViewById(R.id.ButtonmNavMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);




        send_message = (Button) findViewById(R.id.send_message);
        //message_to = (EditText) findViewById(R.id.message_to);
        message_body = (EditText) findViewById(R.id.message_body);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });







    }

    private void sendMessage() {

        //message_to = (EditText) findViewById(R.id.message_to);
        message_body = (EditText) findViewById(R.id.message_body);

        //String receiver_text = message_to.getText().toString();
        String message_body_text = message_body.getText().toString();


        // Receiver is required
        /*if (TextUtils.isEmpty(receiver_text)) {
            Toast.makeText(getApplicationContext(),"Receiver User required",Toast.LENGTH_SHORT).show();
            return;
        }*/

        // Message is required
        if (TextUtils.isEmpty(message_body_text)) {
            Toast.makeText(getApplicationContext(),"message body required",Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT).show();

        final String userId = user.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        // [START_EXCLUDE]
                        if (user == null) {

                            Toast.makeText(sendMessageActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            Intent intent = getIntent();
                            String receiver_uid = intent.getStringExtra("uid");
                            String receiver_mail = intent.getStringExtra("receiver_mail");

                            String senderName = user.getEmail();


                            long msTime = System.currentTimeMillis();
                            Date curDateTime = new Date(msTime);
                            SimpleDateFormat formatter = new SimpleDateFormat("dd'/'MM'/'y hh:mm");
                            String dateTime = formatter.format(curDateTime);


                            sendNewMessage(userId, message_body_text,dateTime,receiver_uid,senderName,receiver_mail);


                        }

                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });






    }


    private String sendNewMessage(String senderId, String messageText,String time,String receiver, String senderName, String receiver_mail){

        String key = mDatabase.child("messages/"+senderId).push().getKey();
        Message message = new Message(messageText, senderId, time, receiver,senderName);
        Map<String, Object> messageValues = message.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/posts/" + key, messageValues);
        childUpdates.put("/messages/" + receiver + "/" + senderName.replace(".", ",") +"/"+ key, messageValues);
        childUpdates.put("/messages/" + user.getUid() + "/" + receiver_mail.replace(".", ",") +"/"+ key, messageValues);

        mDatabase.updateChildren(childUpdates);
        startActivity(new Intent(sendMessageActivity.this, FeedActivity.class));
        return key;


    }

    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod=new

            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.profile:
                            Intent intent_4=new Intent(sendMessageActivity.this,Profile.class);
                            startActivity(intent_4);

                            break;
                        case R.id.feed:
                            Intent intent_1=new Intent(sendMessageActivity.this,FeedActivity.class);
                            startActivity(intent_1);
                            break;
                        case R.id.new_post:
                            //post oluşturma aktivitesi için
                            Intent intent_2=new Intent(sendMessageActivity.this,createPostActivity.class);
                            startActivity(intent_2);
                            break;
                        case R.id.signout:
                            FirebaseAuth.getInstance().signOut();//signout
                            Intent intent_3=new Intent(sendMessageActivity.this,MainActivity.class);
                            startActivity(intent_3);
                            break;
                        case R.id.message:
                            //post oluşturma aktivitesi için
                            Intent intent_5=new Intent(sendMessageActivity.this,MessageActivity.class);
                            startActivity(intent_5);
                            break;



                    }
                    return false;
                }
            };

}