package com.example.manantrivedi.emergencymedicalservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class homePage extends AppCompatActivity{

    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        initAlertButton();






        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }

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
                    dispatchTakeVideoIntent();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //record video for an additional minute
                    //send location
                    // contact emergency contacts
                    bAlert.setText("Video Saved");
                }

                return true;
            }
        });

    }





    public void openMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
