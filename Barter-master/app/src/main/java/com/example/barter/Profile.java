package com.example.barter;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.barter.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class Profile extends AppCompatActivity {
    public String your_num,your_address,your_country;
    TextView receiver_msg ,receiver_2, receiver_3, receiver_4, receiver_5;
    String yourNumber,yourAddress,yourCity,yourTown;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;

        String str;
        String str2;
        String str4 ;
        String str5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bottomNavigationView=findViewById(R.id.ButtonmNavMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        receiver_msg = (TextView)findViewById(R.id.received_number_id);
        receiver_3 = (TextView)findViewById(R.id.received_address_id);
        receiver_4 = (TextView)findViewById(R.id.received_city_id);
        receiver_5 = (TextView)findViewById(R.id.received_town_id);


        //Query query = databaseReference.child("Users").orderByKey().equalTo(userId);
        databaseReference.child("Users").orderByKey().equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    System.out.println("number is :" + snapshot.child("number").getValue() );
                    str = (String)snapshot.child("number").getValue();
                    str2 = (String)snapshot.child("address").getValue();
                    str4 = (String)snapshot.child("city").getValue();
                    str5 = (String)snapshot.child("town").getValue();

                    yourNumber= "your tel. number:" + " " + str;
                    yourAddress= "your adress:" + " " + str2;
                    yourCity= "your city:" + " " + str4;
                    yourTown= "your town:" + " " + str5;


                    // display the string into textView
                    receiver_msg.setText(yourNumber);
                    receiver_3.setText(yourAddress);
                    receiver_4.setText(yourCity);
                    receiver_5.setText(yourTown);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });





    }


    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod=new

            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.profile:
                            break;
                        case R.id.feed:
                            Intent intent_1=new Intent(Profile.this,FeedActivity.class);
                            startActivity(intent_1);
                            break;
                        case R.id.new_post:
                            //post oluşturma aktivitesi için
                            Intent intent_2=new Intent(Profile.this,createPostActivity.class);
                            startActivity(intent_2);
                            break;
                        case R.id.signout:
                            FirebaseAuth.getInstance().signOut();//signout
                            Intent intent_3=new Intent(Profile.this,MainActivity.class);
                            startActivity(intent_3);
                            break;
                        case R.id.message:
                            //post oluşturma aktivitesi için
                            Intent intent_4=new Intent(Profile.this,MessageActivity.class);
                            startActivity(intent_4);
                            break;



                    }
                    return false;
                }
            };





    public void profileResult(View view){

        Intent turn = new Intent(this,CreateProfile.class);
        startActivity(turn);
    }

    public void gotoFeed(View view){

        Intent turn2 = new Intent(this,FeedActivity.class);
        startActivity(turn2);
    }
}