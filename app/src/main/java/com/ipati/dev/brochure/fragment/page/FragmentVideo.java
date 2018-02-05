package com.ipati.dev.brochure.fragment.page;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ipati.dev.brochure.OnItemClickPlayVideo;
import com.ipati.dev.brochure.OnTrickUpdateAdapter;
import com.ipati.dev.brochure.OnTrickUpdateAdapterVideo;
import com.ipati.dev.brochure.R;
import com.ipati.dev.brochure.adapter.RecyclerAdapterVideo;
import com.ipati.dev.brochure.model.Content;

import java.util.ArrayList;

public class FragmentVideo extends Fragment implements OnItemClickPlayVideo, OnTrickUpdateAdapterVideo {
    private final static String KEY = "Video";
    RecyclerView mRecyclerView;
    ArrayList<String> listHeader, listImageUrl, listDate;
    DatabaseReference Ref;
    Query mRef;
    ChildEventListener mChildEventListener;
    String keyInfo;
    OnClickLoadVideo mOnLoadingVideo;

    public static FragmentVideo newInstance(String value) {
        FragmentVideo fragmentVideo = new FragmentVideo();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, value);
        fragmentVideo.setArguments(bundle);
        return fragmentVideo;
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = mInflater.inflate(R.layout.activity_fragment_video, container, false);
        initInstance(view);
        return view;
    }

    private void initInstance(View view) {
        Bundle bundle = getArguments();
        keyInfo = bundle.getString(KEY);
        Ref = FirebaseDatabase.getInstance().getReference();
        mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Video").limitToFirst(10);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_video);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mOnLoadingVideo = (OnClickLoadVideo) getActivity();
    }

    private ChildEventListener mChildEventListener() {
        mChildEventListener = new ChildEventListener() {
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
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        return mChildEventListener;
    }

    private void Data(String key) {
        Query mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Video").child(key).limitToFirst(10);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Content content = dataSnapshot.getValue(Content.class);
                if (content != null) {
                    listHeader.add(content.getTitle());
                    listImageUrl.add(content.getCoverImageUrl());
                    listImageUrl.add(String.valueOf(content.getCreateDate()));

                    final RecyclerAdapterVideo adapter = new RecyclerAdapterVideo(getActivity(), listHeader
                            , listImageUrl, listDate, FragmentVideo.this);
                    mRecyclerView.setAdapter(adapter);
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
        mRef.addChildEventListener(mChildEventListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRef != null) {
            mRef.removeEventListener(mChildEventListener);
        }
    }

    @Override
    public void OnItemClickPlay(String path) {
        Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
        mOnLoadingVideo.OnClickLoadingVideo(path);
    }


    @Override
    public void OnTrick(String key) {
        keyInfo = key;
        Query mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Video").limitToFirst(10);
        mRef.addChildEventListener(mChildEventListener());
    }

    public interface OnClickLoadVideo {
        void OnClickLoadingVideo(String path);
    }
}
