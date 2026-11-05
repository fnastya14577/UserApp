package com.example.userapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class LoadedUserDataActivity extends AppCompatActivity {
    MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loaded_user_data);
        Init();
    }

    private void Init() {
        mPlayer = MediaPlayer.create(this, R.raw.noise);
        mPlayer.setVolume(0.2f, 0.2f);
        mPlayer.setLooping(true);
        mPlayer.start();

        ImageView imageView = findViewById(R.id.imageForLoaded);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_rotate_animation);
        imageView.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadedUserDataActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPlayer != null) mPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null && mPlayer.isPlaying()) mPlayer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null && mPlayer.isPlaying()) mPlayer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
    }
}