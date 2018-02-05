package com.ipati.dev.brochure.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.ipati.dev.brochure.R;
import com.ipati.dev.brochure.adapter.RecyclerAdapterBrochure;
import com.ipati.dev.brochure.model.Content;

import java.util.ArrayList;

public class FragmentAllPagePresentation extends Fragment implements OnTrickUpdateAdapter {
    private final static String KEY = "ALLPRE";
    RecyclerView mRecylcerView;
    ArrayList<String> listHeader, listImageUrl, listDate = new ArrayList<>();
    DatabaseReference Ref, mRef;
    String keyInfo;
    ChildEventListener mChildEvent;

    public static FragmentAllPagePresentation newInstance(String value) {
        FragmentAllPagePresentation fragmentAllPagePresentation = new FragmentAllPagePresentation();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, value);
        fragmentAllPagePresentation.setArguments(bundle);
        return fragmentAllPagePresentation;
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = mInflater.inflate(R.layout.activity_fragment_all_page_presentation, container, false);
        initInstance(view);
        return view;
    }

    private void initInstance(View view) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            keyInfo = bundle.getString(KEY);
        }
        Ref = FirebaseDatabase.getInstance().getReference();
        mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Magazine");

        mRecylcerView = (RecyclerView) view.findViewById(R.id.recycler_view_all_presentation);
        mRecylcerView.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        mRecylcerView.setItemAnimator(new DefaultItemAnimator());


    }


    private ChildEventListener mChildEventListener() {
        mChildEvent = new ChildEventListener() {
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
        return mChildEvent;
    }

    private void Data(String key) {
        DatabaseReference mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Magazine").child(key);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Content content = dataSnapshot.getValue(Content.class);
                if (content != null) {
                    listHeader.add(content.getTitle());
                    listImageUrl.add(content.getCoverImageUrl());
                    listDate.add(String.valueOf(content.getCreateDate()));

                    RecyclerAdapterBrochure adapter = new RecyclerAdapterBrochure(getActivity(), listHeader, listImageUrl, listDate);
                    mRecylcerView.setAdapter(adapter);
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
            mRef.removeEventListener(mChildEvent);
        }
    }


    @Override
    public void OnTrick(Context mContext, FragmentManager fragmentManager, String keyValue) {
        listHeader.clear();
        listImageUrl.clear();
        listDate.clear();

        keyInfo = keyValue;
        DatabaseReference mRef = Ref.child("content-category").child("9876").child(keyInfo).child("Magazine");
        mRef.addChildEventListener(mChildEventListener());
//        Toast.makeText(getContext(), "Trick", Toast.LENGTH_SHORT).show();
    }
}
