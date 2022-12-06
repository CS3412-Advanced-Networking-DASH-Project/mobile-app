package com.hgdnimantha.mobile_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int VIDEO_RECORD_CODE = 101;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //chek for a camera
        if(isCameraAvailable()) {
            Log.i("VIDEO_RECORD_TAG", "Camera is available");
            getCameraPermission();
        }else {
            Log.i("VIDEO_RECORD_TAG", "Camera is not available");
        }
    }

    // on click event for record video button
    public void recordVideoButtonPressed(View view) {
        recordVideo();
    }

    // on click event for watch videos button
    public void watchVideosButtonPressed(View view) {
        Intent intent = new Intent(this, VideoListActivity.class);
        startActivity(intent);
    }

    //check availability of a camera
    private boolean isCameraAvailable() {
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    //get camera permission
    private void getCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    //record a video
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_RECORD_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_RECORD_CODE) {
            if (resultCode == RESULT_OK) {
                videoUri = data.getData();
                Intent intent = new Intent(this, VideoUploadActivity.class);
                intent.putExtra("VIDEO_URI", videoUri.toString());
                startActivity(intent);
                Log.i("VIDEO_RECORD_TAG", "Video saved to: " + videoUri.toString());
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("VIDEO_RECORD_TAG", "Video recording cancelled");
            } else {
                Log.i("VIDEO_RECORD_TAG", "Failed to record video");
            }
        }
    }
}