package com.ipati.dev.brochure;

import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

public class VideoBreaker extends AppCompatActivity implements View.OnTouchListener {
    SimpleExoPlayerView exoViewPlayer;
    SimpleExoPlayer mPlayer;
    DefaultBandwidthMeter bandwidthMeterDefault;
    DefaultExtractorsFactory mExtractorsFactory;
    File file;
    ExtractorMediaSource mMediaSource;
    DataSource.Factory dataSourceFactory;
    LinearLayout mLinearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_breaker);
        exoViewPlayer = (SimpleExoPlayerView) findViewById(R.id.exo_view_player_breaker);
        mLinearlayout = (LinearLayout) findViewById(R.id.touch_view);
        exoViewPlayer.setOnTouchListener(this);


        initialPlayer(savedInstanceState);


    }

    private void initialPlayer(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            long duration = savedInstanceState.getLong("seekto");
//            Toast.makeText(getApplicationContext(), String.valueOf(duration), Toast.LENGTH_SHORT).show();

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory tracker = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(tracker);
            mPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);

            mPlayer.setPlayWhenReady(true);
            exoViewPlayer.setPlayer(mPlayer);


            //Todo:Create Source Player
            bandwidthMeterDefault = new DefaultBandwidthMeter();

            dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext()
                    , Util.getUserAgent(getApplicationContext(), "BrochureVideo"), bandwidthMeterDefault);

            mExtractorsFactory = new DefaultExtractorsFactory();
            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if (isSDPresent) {
                file = new File(Environment.getExternalStorageDirectory() + "/EggVideoFileBackground/" + "backgroundVideo.mp4");
            } else {
                file = new File(Environment.getDataDirectory() + "/EggVideoFileBackground/" + "backgroundVideo.mp4");
            }

            mMediaSource = new ExtractorMediaSource(Uri.fromFile(file), dataSourceFactory, mExtractorsFactory
                    , null, null);
            mPlayer.prepare(mMediaSource);
            mPlayer.seekTo(duration);
        } else {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelection.Factory tracker = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(tracker);


            mPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
            mPlayer.setPlayWhenReady(true);
            exoViewPlayer.setPlayer(mPlayer);


            //Todo:Create Source Player
            bandwidthMeterDefault = new DefaultBandwidthMeter();

            dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext()
                    , Util.getUserAgent(getApplicationContext(), "BrochureVideo"), bandwidthMeterDefault);

            mExtractorsFactory = new DefaultExtractorsFactory();

            file = new File(Environment.getExternalStorageDirectory() + "/EggVideoFileBackground/" + "backgroundVideo.mp4");


            mMediaSource = new ExtractorMediaSource(Uri.fromFile(file), dataSourceFactory, mExtractorsFactory
                    , null, null);
            mPlayer.setPlayWhenReady(true);
            mPlayer.seekToDefaultPosition();
            mPlayer.prepare(mMediaSource);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("seekto", mPlayer.getCurrentPosition());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }

    }
}
