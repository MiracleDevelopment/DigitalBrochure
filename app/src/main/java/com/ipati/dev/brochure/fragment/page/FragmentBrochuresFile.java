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
import com.ipati.dev.brochure.OnTrickUpdateAdapter;
import com.ipati.dev.brochure.model.Content;
import com.ipati.dev.brochure.R;
import com.ipati.dev.brochure.adapter.RecyclerAdapterBrochure;

import java.util.ArrayList;

public class FragmentBrochuresFile extends Fragment implements OnTrickUpdateAdapter {
    private final static String KEY = "File";
    RecyclerView mRecyclerView;
    ArrayList<String> listHeader, listImageUrl, listDate;
    DatabaseReference Ref;
    Query mRef;
    ChildEventListener mChildListener;
    String keyInfo;

    public static FragmentBrochuresFile newInstance(String value) {
        FragmentBrochuresFile fragmentBrochuresFile = new FragmentBrochuresFile();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, value);
        fragmentBrochuresFile.setArguments(bundle);
        return fragmentBrochuresFile;
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = mInflater.inflate(R.layout.activity_fragment_brochures_file, container, false);
        initInstance(view);
        return view;
    }

    private void initInstance(View view) {
        Bundle bundle = getArguments();
        keyInfo = bundle.getString(KEY);
        Ref = FirebaseDatabase.getInstance().getReference();
        mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Magazine").limitToFirst(10);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_promotion);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    private ChildEventListener mChildEventListener() {
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Data(key);
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
        return mChildListener;
    }

    private void Data(String key) {
        Query mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Magazine").child(key).limitToFirst(10);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Content content = dataSnapshot.getValue(Content.class);
                if (content != null) {
                    listHeader.add(content.getTitle());
                    listImageUrl.add(content.getCoverImageUrl());
                    listDate.add(String.valueOf(content.getCreateDate()));

                    RecyclerAdapterBrochure adapter = new RecyclerAdapterBrochure(getActivity(), listHeader, listImageUrl, listDate);
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
            mRef.removeEventListener(mChildListener);
        }
    }


    @Override
    public void OnTrick(Context mContext, FragmentManager fragmentManager, String keyValue) {
        listHeader = new ArrayList<>();
        listImageUrl = new ArrayList<>();
        listDate = new ArrayList<>();

        keyInfo = keyValue;
        Query mQuery = Ref.child("content-category").child("9876").child(keyValue).child("Magazine").limitToFirst(10);
        mQuery.addChildEventListener(mChildEventListener());
    }
}

