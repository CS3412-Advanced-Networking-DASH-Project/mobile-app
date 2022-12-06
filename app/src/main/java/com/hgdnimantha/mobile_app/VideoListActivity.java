package com.hgdnimantha.mobile_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hgdnimantha.mobile_app.databinding.ActivityVideoListBinding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoListActivity extends AppCompatActivity {

    private ActivityVideoListBinding binding;
    private LinearLayout layout;
    private ArrayList<Video> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoList = new ArrayList<>();
        getVideoList();

        binding = ActivityVideoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("STREAMER");

        layout = findViewById(R.id.video_container);

    }


    private void addVideoCard(Video video) {

        View videoCard = getLayoutInflater().inflate(R.layout.video_card, null);

        TextView title = videoCard.findViewById(R.id.titleCard);
        TextView desc = videoCard.findViewById(R.id.descriptionCard);
        ImageView thumbnail = videoCard.findViewById(R.id.thumbnail);

        // update the card with the video details

        DownloadImageFromInternet downloadImageFromInternet = new DownloadImageFromInternet(thumbnail);
        downloadImageFromInternet.execute(BuildConfig.FILE_SYSTEM_URL + "thumbnails/" + video.getID() + ".jpg");

        title.setText(video.getTitle());
        desc.setText(video.getDescription());
        videoCard.setTag(video);

        // adding new card to the layout
        layout.addView(videoCard);
    }

    private void getVideoList() {

        String Tag = "VIDEO_LIST";
        Log.i(Tag, "Starting");
        Call<List<Video>> call = RetrofitClient.getInstance().getAPI().getVideos();
        Log.i(Tag, "call ");
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                Log.i(Tag, "onResponse ");
                List<Video> videoList = response.body();
                for (int i = 0; i < videoList.size(); i++) {
                    addVideoCard(videoList.get(i));
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {

                Log.e(Tag, t.getMessage());
                Log.e(Tag, call.toString());
            }

        });
    }

    public void cardOnClick(View view) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        Video video = (Video) view.getTag();
        intent.putExtra("VIDEO_ID", video.getID());
        intent.putExtra("VIDEO_TITLE", video.getTitle());
        startActivity(intent);
    }


    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage= BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}