package com.example.barter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.barter.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class createPostActivity extends AppCompatActivity  {

    private DatabaseReference mDatabase;
    private GpsTracker gpsTracker;
    BottomNavigationView bottomNavigationView;

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private final int PICK_IMAGE_REQUEST = 71;
    public static String str1;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FusedLocationProviderClient fusedLocationProviderClient;
    double latitude;
    double longitude;


    // ...
    EditText bodyText,priceValue;
    Button uploadPhoto,uploadPost;
    ImageView imageView;

    FirebaseStorage storage;
    StorageReference storageReference;


    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        bottomNavigationView=findViewById(R.id.ButtonmNavMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        // [END initialize_database_ref]
        uploadPost = (Button) findViewById(R.id.uploadPost);
        uploadPhoto = (Button) findViewById(R.id.uploadPhoto);
        imageView = (ImageView) findViewById(R.id.imageView);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }




        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });






    }
    private void submitPost() {

        bodyText = (EditText) findViewById(R.id.postText);
        priceValue = (EditText) findViewById(R.id.price);
        uploadPhoto = (Button) findViewById(R.id.uploadPhoto);
        uploadPost = (Button) findViewById(R.id.uploadPost);

        String body = bodyText.getText().toString();
        String price = priceValue.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(getApplicationContext(),"Price required",Toast.LENGTH_SHORT).show();
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            Toast.makeText(getApplicationContext(),"Text required",Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button so there are no multi-posts
        //setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = user.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        //User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(createPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post

                            String image_uri = uploadImage();
                            writeNewPost(userId, user.getEmail(), body, price,image_uri);


                        }

                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private String uploadImage() {
        String path = "null";
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            //progressDialog.show();

            path = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/"+ path);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            Toast.makeText(createPostActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressDialog.dismiss();
                            Toast.makeText(createPostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            //progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }

        return path;
    }

    private String writeNewPost(String userId, String username, String body, String price, String image_uri) {

        double[] locations = getLocation();
        double userLat = locations[0];
        double userLong = locations[1];

        System.out.println("lat is :" + userLat);


        String key = mDatabase.child("posts").push().getKey();
        Post post2 = new Post(userId, username, body, price, userLat, userLong,image_uri);
        Map<String, Object> postValues = post2.toMap();
        //postValues.put("userLat",userLat);
        //postValues.put("userLong",userLong);
        //postValues.put("image_uri",image_uri);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
        startActivity(new Intent(createPostActivity.this, FeedActivity.class));
        return key;
    }

    public double[] getLocations(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // I suppressed the missing-permission warning because this wouldn't be executed in my
        // case without location services being enabled
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        try
        {
            Thread.sleep(5000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        double userLat = lastKnownLocation.getLatitude();
        double userLong = lastKnownLocation.getLongitude();

        double[] location = new double[2];
        location[0] = userLat;
        location[1] = userLong;


        System.out.println(" lat is : " + userLat + "long is : " + userLong );
        return location;
    }

    public  double[] getLocation(){
        double latitude;
        double longitude;
        double[] location = new double[2];
        gpsTracker = new GpsTracker(createPostActivity.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            location[0] = latitude;
            location[1] = longitude;
            System.out.println("ohoooo lat is : " + latitude + "long is : " + longitude );

        }else{
            System.out.println("haydaaa");
            gpsTracker.showSettingsAlert();

        }
        return location;

    }

    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod=new

            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.new_post:
                            break;
                        case R.id.feed:
                            //post oluşturma aktivitesi için
                            Intent intent_1=new Intent(createPostActivity.this,FeedActivity.class);
                            startActivity(intent_1);
                            break;

                        case R.id.profile:
                            //post oluşturma aktivitesi için
                            Intent intent_2=new Intent(createPostActivity.this,Profile.class);
                            startActivity(intent_2);
                            break;
                        case R.id.signout:
                            FirebaseAuth.getInstance().signOut();//signout
                            Intent intent_3=new Intent(createPostActivity.this,MainActivity.class);
                            startActivity(intent_3);
                            break;
                        case R.id.message:
                            //post oluşturma aktivitesi için
                            Intent intent_4=new Intent(createPostActivity.this,MessageActivity.class);
                            startActivity(intent_4);
                            break;



                    }
                    return false;
                }
            };


}