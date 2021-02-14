package com.example.barter;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class CreateProfile extends AppCompatActivity {

    EditText  address, country, city, town ,number;
    TextView result_number, result_address,result_country;
    String input_number, input_address, input_country, input_city, input_town;
    Button send_button;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        send_button = (Button)findViewById(R.id.to_profile);
        number = findViewById(R.id.number);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        town = findViewById(R.id.town);
        firebaseAuth = FirebaseAuth.getInstance();





        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {




                input_number = number.getText().toString();
                input_address = address.getText().toString();
                input_city = city.getText().toString();
                input_town = town.getText().toString();

                String user_id = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference current_user = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                Map newPost = new HashMap();
                newPost.put("number",input_number);
                newPost.put("address",input_address);
                newPost.put("city",input_city);
                newPost.put("town",input_town);
                newPost.put("uid",user_id);
                current_user.setValue(newPost);






                // Create the Intent object of this class Context() to Second_activity class
                Intent intent2 = new Intent(getApplicationContext(), Profile.class);

                // now by putExtra method put the value in key, value pair
                // key is message_key by this key we will receive the value, and put the string

                //intent2.putExtra("message_key", input_number);
                //intent2.putExtra("message_2", input_address);
                //intent2.putExtra("message_4", input_city);
                //intent2.putExtra("message_5", input_town);


                // start the Intent
                startActivity(intent2);
            }
        });

    }
}