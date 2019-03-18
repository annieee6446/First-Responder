package com.example.notificationbutton;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.telephony.SmsManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//
//import com.google.android.gms.tasks.Continuation;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.mongodb.lang.NonNull;
//import com.mongodb.stitch.android.core.Stitch;
//import com.mongodb.stitch.android.core.StitchAppClient;
//import com.mongodb.stitch.android.core.auth.StitchUser;
//import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
//import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
//import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
//import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
//import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
//import com.mongodb.stitch.android.core.auth.StitchUser;
//import com.mongodb.stitch.android.core.Stitch;
//import com.mongodb.stitch.android.core.StitchAppClient;
//import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
//
//import org.bson.Document;
//
//import java.util.ArrayList;
//import java.util.List;

public class MainActivity extends AppCompatActivity {
    //StitchAppClient stitchClient = null;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAlertButton();
//        this.stitchClient = Stitch.getDefaultAppClient();
//
//        Log.d("stitch", "logging in anonymously");
//        stitchClient.getAuth().loginWithCredential(new AnonymousCredential()
//        ).continueWithTask(new Continuation<StitchUser, Task<Void>>() {
//            @Override
//            public Task<Void> then(@NonNull Task<StitchUser> task) throws Exception {
//                if (task.isSuccessful()) {
//                    Log.d("stitch", "logged in anonymously as user " + task.getResult());
//                } else {
//                    Log.e("stitch", "failed to log in anonymously", task.getException());
//                }
//                return null;
//            }
//        });

    }

    // Get a previously defined client or create one
    //final StitchAppClient client = Stitch.getDefaultAppClient();

// Log-in using an Anonymous authentication provider from Stitch
//    client.getAuth().loginWithCredential(new AnonymousCredential()).addOnCompleteListener(new OnCompleteListener<StitchUser>() {
//        @Override
//        public void onComplete (@NonNull Task < StitchUser > task){
//            // Get a remote client
//            final RemoteMongoClient remoteMongoClient =
//                    client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
//
//            // Set up the atlas collection
//            RemoteMongoCollection remoteCollection = remoteMongoClient
//                    .getDatabase("FirstResponderV2").getCollection("IncidentVideoV2");
//          }
//    }


    static final int REQUEST_VIDEO_CAPTURE = 1;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }

    }

    private void initAlertButton() {
        final Button bAlert = (Button) findViewById(R.id.alertButton);

        bAlert.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                   // record video
                   // dispatchTakeVideoIntent(); // Commented out to text messaging functionality
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //record video for an additional minute
                    //send location
                    // contact emergency contacts
                    bAlert.setText("Video Saved");
                    sendSMSmessage();
                }

                return true;
            }
        });

    }

    //Get permission
    protected void sendSMSmessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

// Send actual message
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    // Need to pull username and emergency contact phone number from database
                    smsManager.sendTextMessage("4783978219", null, "TEST MESSAGE. CALL 911! USER has activated an Emergency Beacon: " +
                            "Location:______" , null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
}
