// THis is a fragment that will contain a button that when held records video and when released sends a text, saves the location and saves the video

package com.example.firstrespondertest;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("ALL")
public class EmergencyFragment extends Fragment implements  SurfaceHolder.Callback{

Camera camera;

SurfaceView mSurfaceView;
SurfaceHolder mSurfaceHolder;




final int CAMERA_REQUEST_CODE = 1;
public static EmergencyFragment newInstance(){
    EmergencyFragment fragment = new EmergencyFragment();
    return fragment;
}


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

      mSurfaceView = view.findViewById(R.id.surfaceView);
      mSurfaceHolder = mSurfaceView.getHolder();

       if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
        //request manual permission from user
        ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

       }else{
           mSurfaceHolder.addCallback(this);
           mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       }



        return view;
    }





    //Three methods below are needed for addCallback(this) and come from implements  SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    camera = Camera.open();

    Camera.Parameters parameters;
    parameters = camera.getParameters();

    //
    camera.setDisplayOrientation(90);

        List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
        Camera.Size size = allSizes.get(0); // get top size
        for (int i = 0; i < allSizes.size(); i++) {
            if (allSizes.get(i).width > size.width)
                size = allSizes.get(i);
        }
   //set max Picture Size
    parameters.setPictureSize(size.width, size.height);

    parameters.setPreviewFrameRate(30);
   // parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

    camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSurfaceHolder.addCallback(this);
                    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }else{
                    Toast.makeText(getContext(), "Must provide permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }



}


