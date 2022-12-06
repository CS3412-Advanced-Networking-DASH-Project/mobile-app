package com.hgdnimantha.mobile_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoUploadActivity extends AppCompatActivity {

    private String videoUri;
    private EditText videoTitle;
    private EditText videoDesc;
    private ExoPlayer player;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        videoTitle = findViewById(R.id.video_title);
        videoDesc = findViewById(R.id.video_desc);


        // Get URL for the video
        videoUri = Uri.parse(getIntent().getStringExtra("VIDEO_URI")).toString();

        StyledPlayerView playerView = findViewById(R.id.video_player_view_upload_form);
        playerView.setControllerShowTimeoutMs(3000);

        player = new ExoPlayer.Builder(this).build();

        playerView.setPlayer(player);

        MediaItem mediaItem = new MediaItem.Builder().setUri(videoUri).build();

        player.setMediaItem(mediaItem);

        player.prepare();

        player.setPlayWhenReady(true);


    }


    private class UploadVideoFile extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            videoUri = getFilePathFromUri(Uri.parse(videoUri));

            Log.i("MY_VIDEO_PATH", videoUri);

            try {

                File video_source = new File(videoUri);
                String Tag="UPLOAD";

                Map<String, RequestBody> map = new HashMap<>();
                map.put("title", RequestBody.create(MediaType.parse("text/plain"), videoTitle.getText().toString()));
                map.put("description", RequestBody.create(MediaType.parse("text/plain"), videoDesc.getText().toString()));

                RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), video_source);
                Log.i(Tag, "Request body created");
                MultipartBody.Part partImage = MultipartBody.Part.createFormData("video", video_source.getName(), reqBody);
                Log.i(Tag, "Multipart created");
                API api = RetrofitClient.getInstance().getAPI();
                Log.i(Tag, "API created");
                Call<ResponseBody> upload = api.uploadVideo(partImage, map);
                Log.i(Tag, "upload response body created");
                upload.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if(response.isSuccessful()) {
                            createNotification("VIDEO UPLOAD", "Video was successfully uploaded to the server!");
                        }else{
                            createNotification("VIDEO UPLOAD", "Video could not upload to the server!");
                            Log.e(Tag, "Response unsuccessful");
                            Log.e(Tag, response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        createNotification("VIDEO UPLOAD", "Video could not upload to the server!");
                        Log.e(Tag, "Mission failed");
                        Log.e(Tag, t.getMessage());
                    }
                });

            }catch(Exception e){
                Log.e("ERROR", "URL error: " + e.getMessage(), e);
                createNotification("VIDEO UPLOAD", "Video could not upload to the server!");
            }

            return null;
        }
    }

    // upload button onclick callback
    public void uploadButtonClicked(View view) {
        verifyStoragePermissions(this);
        Log.i("ONCLICK","Headers are written 1");
        UploadVideoFile uploadVideoFile = new UploadVideoFile();
        uploadVideoFile.execute();

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

    public void cancelButtonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    // get file path from uri
    public String getFilePathFromUri(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // create a notification
    private void createNotification(String title, String desc)
    {
        String id = "channel_id_01";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel =manager.getNotificationChannel(id);
            if(channel ==null)
            {
                channel = new NotificationChannel(id,"Channel Title", NotificationManager.IMPORTANCE_HIGH);
                //config nofication channel
                channel.setDescription("[Channel description]");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent = new Intent(this,NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id)
                .setSmallIcon(R.mipmap.launcher_icon_03_round)
                .setContentTitle(title)
                .setContentText(desc)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(false)//true touch on notificaiton menu dismissed, but swipe to dismiss
                .setTicker("Nofiication");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m = NotificationManagerCompat.from(getApplicationContext());
        //id to generate new notification in list notifications menu
        m.notify(new Random().nextInt(),builder.build());

    }
}