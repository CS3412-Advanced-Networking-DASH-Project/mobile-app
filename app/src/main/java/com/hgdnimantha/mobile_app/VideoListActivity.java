package com.hgdnimantha.mobile_app;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hgdnimantha.mobile_app.databinding.ActivityVideoListBinding;

import java.util.ArrayList;


public class VideoListActivity extends AppCompatActivity {

    private ActivityVideoListBinding binding;
    private LinearLayout layout;
    private ArrayList<Integer> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVideoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("STREAMER");


        layout = findViewById(R.id.video_container);

        videoList = new ArrayList<>();
        videoList.add(1);
        videoList.add(1);
        videoList.add(1);
        videoList.add(1);
        videoList.add(1);
        videoList.add(1);
        videoList.add(1);
        videoList.add(1);

        for(Integer element : videoList) {
            addVideoCard("Dasun", "Nimantha");
        }

    }


    private void addVideoCard(String titleText, String descText) {

        View videoCard = getLayoutInflater().inflate(R.layout.video_card, null);
        layout.addView(videoCard);

        TextView title = findViewById(R.id.titleCard);
        TextView desc = findViewById(R.id.descriptionCard);

//        title.setText(titleText);
//        desc.setText(descText);


    }
}