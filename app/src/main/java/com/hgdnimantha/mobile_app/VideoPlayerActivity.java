package com.hgdnimantha.mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class VideoPlayerActivity extends AppCompatActivity {

    private ImageButton qualityButton;
    private ExoPlayer videoPlayer;
    private TextView title;
    private boolean isShowingTrackSelectionDialog;
    private Handler handler;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_avtivity);

        title = findViewById(R.id.video_title);

        StyledPlayerView playerView = findViewById(R.id.video_player_view);
        playerView.setControllerShowTimeoutMs(2000);

        qualityButton = findViewById(R.id.video_quality);
        isShowingTrackSelectionDialog = false;

        String videoTitle = getIntent().getStringExtra("VIDEO_TITLE");
        String videoId = getIntent().getStringExtra("VIDEO_ID");

        title.setText(videoTitle);


        TrackSelector tackSelector = new DefaultTrackSelector();

        videoPlayer = new ExoPlayer.Builder(this).setTrackSelector(tackSelector).build();

        playerView.setPlayer(videoPlayer);

        MediaItem item = new MediaItem.Builder().setUri(BuildConfig.FILE_SYSTEM_URL  + videoId + "/" + videoId + "_out.mpd").build();

        videoPlayer.setMediaItem(item);
        videoPlayer.prepare();
        videoPlayer.setPlayWhenReady(true);

        handler = new Handler();
        runnable = () -> {
            qualityButton.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        };

        startHandler();


    }

    private void startHandler(){
        handler.postDelayed(runnable, 3000);
    }

    private void stopHandler(){
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        title.setVisibility(View.VISIBLE);
        qualityButton.setVisibility(View.VISIBLE);
        stopHandler();
        startHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoPlayer.stop();
    }


    public void qualityButtonOnClickHandler(View view) {
        Log.i("QUALITY", TrackSelectionDialog.willHaveContent(videoPlayer) + "");
        if (!isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(videoPlayer)) {
            Log.i("QUALITY", "QUALITY");
            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForPlayer(
                            videoPlayer,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
            trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
        }

    }
}