package com.ipati.dev.brochure.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ipati.dev.brochure.OnItemClickPlayVideo;
import com.ipati.dev.brochure.OnTrickUpdateAdapter;
import com.ipati.dev.brochure.R;
import com.ipati.dev.brochure.adapter.RecyclerAdapterVideo;

import com.ipati.dev.brochure.model.Content;

import java.io.File;
import java.util.ArrayList;

public class FragmentAllPageVideo extends Fragment implements OnItemClickPlayVideo, OnTrickUpdateAdapter {
    public final static String KEY = "AllPageVideo";
    RecyclerView mRecyclerViewVideo;
    ArrayList<String> listHeader, listImageUrl, listDate;
    DatabaseReference Ref, mRef;
    String keyInfo, pathVideo;
    ChildEventListener mChild;
    SimpleExoPlayerView mSimpleExoPlayerView;
    public SimpleExoPlayer player;
    DefaultBandwidthMeter bandwidthMeterDefault;
    DataSource.Factory dataSourceFactory;
    ExtractorsFactory mExtractorsFactory;
    MediaSource mMediaSource;
    File file;

    public static FragmentAllPageVideo newInstance(String value, String path) {
        FragmentAllPageVideo fragmentAllPageVideo = new FragmentAllPageVideo();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, value);
        bundle.putString("pathvideo", path);
        fragmentAllPageVideo.setArguments(bundle);
        return fragmentAllPageVideo;
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = mInflater.inflate(R.layout.activity_fragment_all_page_video, container, false);
        initInstance(view);
        return view;
    }

    private void initInstance(View view) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            keyInfo = bundle.getString(KEY);
            pathVideo = bundle.getString("pathvideo");
        }
        Ref = FirebaseDatabase.getInstance().getReference();
        mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Video");
        mSimpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.exo_player_video_view);
        mRecyclerViewVideo = (RecyclerView) view.findViewById(R.id.recycler_view_video_all);
        mRecyclerViewVideo.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewVideo.setItemAnimator(new DefaultItemAnimator());
        initializePlayer(pathVideo);

    }

    private void initializePlayer(String pathVideo) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        TrackSelection.Factory tracker = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(tracker);

        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        player.setPlayWhenReady(true);
        mSimpleExoPlayerView.setPlayer(player);


        //Todo:Create Source Player
        bandwidthMeterDefault = new DefaultBandwidthMeter();

        dataSourceFactory = new DefaultDataSourceFactory(getContext()
                , Util.getUserAgent(getContext(), "Brochure"), bandwidthMeterDefault);

        mExtractorsFactory = new DefaultExtractorsFactory();

        if (pathVideo != null) {
            file = new File(Environment.getExternalStorageDirectory() + "/EggBrochureVideo/" + pathVideo);
            Toast.makeText(getContext(), pathVideo, Toast.LENGTH_SHORT).show();
        } else {
            file = new File(Environment.getExternalStorageDirectory() + "/EggBrochureVideo/" + "CRM.mp4");
        }

        mMediaSource = new ExtractorMediaSource(Uri.fromFile(file), dataSourceFactory, mExtractorsFactory
                , null, null);
        player.prepare(mMediaSource);


    }

    private ChildEventListener mChildEvent() {
        mChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String Key = dataSnapshot.getKey();
                Data(Key);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return mChild;
    }

    private void Data(String key) {
        DatabaseReference mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Video").child(key);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Content content = dataSnapshot.getValue(Content.class);
                if (content != null) {
                    listHeader.add(content.getTitle());
                    listImageUrl.add(content.getCoverImageUrl());
                    listDate.add(String.valueOf(content.getCreateDate()));

                    RecyclerAdapterVideo adapter = new RecyclerAdapterVideo(getActivity(), listHeader
                            , listImageUrl, listDate, FragmentAllPageVideo.this);
                    mRecyclerViewVideo.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        listHeader = new ArrayList<>();
        listImageUrl = new ArrayList<>();
        listDate = new ArrayList<>();
        mRef.addChildEventListener(mChildEvent());

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();



    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStop", "Work");
        if (mRef != null) {
            mRef.removeEventListener(mChild);
        }
        if (player != null) {
            player.stop();
            player.release();
        }

    }


    @Override
    public void OnItemClickPlay(String path) {
        SimpleExoPlayerView simpleExoPlayerView = new SimpleExoPlayerView(getContext());
        simpleExoPlayerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSimpleExoPlayerView.removeAllViews();
        mSimpleExoPlayerView.addView(simpleExoPlayerView);
        player.stop();
        player.release();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory tracker = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(tracker);

        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        player.setPlayWhenReady(true);
        simpleExoPlayerView.setPlayer(player);

        DefaultBandwidthMeter bandwidthMeterDefault = new DefaultBandwidthMeter();

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext()
                , Util.getUserAgent(getContext(), "Brochure"), bandwidthMeterDefault);

        ExtractorsFactory mExtractorsFactory = new DefaultExtractorsFactory();
        File file = new File(Environment.getExternalStorageDirectory() + "/EggBrochureVideo/" + path);

        final MediaSource mMediaSource = new ExtractorMediaSource(Uri.fromFile(file), dataSourceFactory, mExtractorsFactory
                , null, null);

        player.prepare(mMediaSource, true, true);
        Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
    }




    @Override
    public void OnTrick(Context mContext, FragmentManager fragmentManager, String keyValue) {
        listHeader = new ArrayList<>();
        listImageUrl = new ArrayList<>();
        listDate = new ArrayList<>();

        keyInfo = keyValue;
        DatabaseReference mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Video");
        ;
        mRef.addChildEventListener(mChildEvent());
//        Toast.makeText(getContext(), "Trick", Toast.LENGTH_SHORT).show();
    }
}
