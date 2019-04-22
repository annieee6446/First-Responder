//This the main activity that holds the fragments

package com.example.firstrespondertest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    // every variable for screen record
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    private Button captureButton;
    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_PERMISSIONS = 10;
    private TextView locationTest;
    private Location location;
    private Criteria criteria;
    private String bestProvider;
    Geocoder geocoder;
    List<Address> addresses;
    public String address;
    public int sendCount = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListner;


    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    // end every variable needed for screen record

    private LocationManager locationManager;

    // connects the viewPager and adapter
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationTest = (TextView) findViewById(R.id.locationTest);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO) + ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (MainActivity.this, Manifest.permission.RECORD_AUDIO) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_PERMISSIONS);
            }
        } else {
            //onToggleScreenShare(v);
        }
       Intent intent =  getIntent();
       String displayname = intent.getStringExtra("displayName");
        locationTest.setText(displayname);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        //location =  locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        //   onLocationChanged(location);
        // View pagers allow for "multiple activities", called fragments, within one activity
        ViewPager viewPager = findViewById(R.id.viewPager);

        //    final Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

        adapterViewPager = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        viewPager.setCurrentItem(0);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;

        mMediaRecorder = new MediaRecorder();

        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);

        geocoder = new Geocoder(this, Locale.getDefault());

        final Button capture = (Button) findViewById(R.id.captureButton);

        capture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // record video

                    initRecorder();
                    shareScreen();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //record video for an additional minute
                    //send location
                    // contact emergency contacts

                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    Log.v(TAG, "Stopping Recording");
                    stopScreenSharing();
                    //  sendSMSmessage(location);
//                    sendSMSmessage();
                    capture.setText("Video Saved");
                   // onLocationChanged(location);
                    getLocation();


                }

                return true;
            }
        });

    }

    // private void sendSMSmessage(Location location) {


    private void sendSMSmessage(String address) {
        SmsManager smsManager = SmsManager.getDefault();
        //    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Need to pull username and emergency contact phone number from database
        //String incidentLocation = formatLocation(location);
        //String usersname = getUsersname();
        smsManager.sendTextMessage("4783978219", null, "TEST MESSAGE. CALL 911! USER has activated an Emergency Beacon: " +
                "Location: " + address, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();

        locationTest.setText("TEST MESSAGE. CALL 911! USER has activated an Emergency Beacon: " +
                "Location: " + address);

        return;

    }

    private String getUsersname() {
     // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
      return currentUserDb.orderByChild("name").toString();
    }

    //    public String formatLocation(Location location){
//        Double longitude = location.getLongitude();
//        String lon = longitude.toString();
//        Double latitude = location.getLatitude();
//        String lat = latitude.toString();
//        String incidentLocation = "Longitude: " + lon + "  " + "Latitude: " + lat;
//
//        return incidentLocation;
//    }
    protected void getLocation() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);


}

    @Override
    public void onLocationChanged(Location location){
       // locationTest = (TextView) findViewById(R.id.textview1);
        double latitude = location.getLatitude();
        double longitude= location.getLongitude();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

//        locationTest.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        //locationTest.setText(address);
        if (sendCount <1){
        sendSMSmessage(address);
        }
        sendCount++;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    private void initRecorder() {
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            //Not saving the video
            mMediaRecorder.setOutputFile(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM
                            ) + "/Camera/video.mp4");
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoFrameRate(30);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot
        // be reused again
        destroyMediaProjection();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyMediaProjection();
    }

    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("MainActivity",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaProjectionCallback = new MediaProjectionCallback();
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }


        // create an adapter. Adapters control view pagers
    public static class PagerAdapter extends FragmentPagerAdapter{

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // returns the fragment based on its position
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    //return fragment home
                    return HomeFragment.newInstance();
                case 1:
                    //return fragment emergency
                    return EmergencyFragment.newInstance();
            }
            return null;
        }

        //Returns number of pages in viewPager.
        // In our case 2, Home page and emergency page
        @Override
        public int getCount() {
            return 2;
        }
    }
    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {

                mMediaRecorder.stop();
                mMediaRecorder.reset();
                Log.v(TAG, "Recording Stopped");

            mMediaProjection = null;
            stopScreenSharing();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                } else {

                }
                return;
            }
        }
    }
}
