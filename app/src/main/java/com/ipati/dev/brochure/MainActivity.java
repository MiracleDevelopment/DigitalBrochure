package com.ipati.dev.brochure;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ipati.dev.brochure.adapter.SpinnerBaseAdapter;
import com.ipati.dev.brochure.fragment.FragmentAllPagePresentation;
import com.ipati.dev.brochure.fragment.FragmentAllPageVideo;
import com.ipati.dev.brochure.fragment.page.FragmentBrochuresFile;
import com.ipati.dev.brochure.fragment.page.FragmentVideo;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseApplication implements View.OnClickListener, FragmentVideo.OnClickLoadVideo
        , View.OnTouchListener {
    Toolbar mToolbar;
    TextView seeAllPresentation, seeAllVideo;
    FrameLayout frameFragmentPresentation, frameFragmentVideo, frameAll;
    Spinner mSpinner;
    boolean statusFragmentPresentation = false;
    boolean statusFragmentVideo = false;
    ArrayList<String> listMenu;
    DatabaseReference Ref, mRef;
    static String keyValue = "CRM";
    LinearLayout mLinearLayout;
    private OnTrickUpdateAdapter mOnTrick;
    private OnTrickUpdateAdapterVideo mOnTrickVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Ref = FirebaseDatabase.getInstance().getReference();
        mRef = Ref.child("content-category").child("9876");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        seeAllPresentation = (TextView) findViewById(R.id.tv_see_all_presentation);
        seeAllVideo = (TextView) findViewById(R.id.tv_see_all_video);
        frameFragmentPresentation = (FrameLayout) findViewById(R.id.frame_fragment_brochure);
        frameFragmentVideo = (FrameLayout) findViewById(R.id.frame_fragment_video);
        frameAll = (FrameLayout) findViewById(R.id.allframefragment);
        mSpinner = (Spinner) findViewById(R.id.sp_list_filter);
        mLinearLayout = (LinearLayout) findViewById(R.id.drawer_layout);
        frameAll.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);

        frameAll.removeAllViews();
        frameFragmentPresentation.removeAllViews();
        frameFragmentVideo.removeAllViews();
        listMenu = new ArrayList<>();


        CallFragment();
        seeAllPresentation.setOnClickListener(this);
        seeAllVideo.setOnClickListener(this);
        mLinearLayout.setOnTouchListener(this);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String menu = data.getKey();
                    listMenu.add(menu);
                }
                SpinnerBaseAdapter adapter = new SpinnerBaseAdapter(getApplicationContext(), listMenu);
                mSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keyValue = listMenu.get(position);

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment_brochure
                        , FragmentBrochuresFile.newInstance(keyValue)).commitNow();

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment_video
                        , FragmentVideo.newInstance(keyValue)).commitNow();


                if (statusFragmentPresentation) {
                    frameFragmentPresentation.setVisibility(View.GONE);
                    frameFragmentVideo.setVisibility(View.GONE);

                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("BrochureFragment");
                    mOnTrick = (OnTrickUpdateAdapter) fragment;
                    mOnTrick.OnTrick(getApplicationContext(), getSupportFragmentManager(), keyValue);
                }

                if (statusFragmentVideo) {
                    frameFragmentPresentation.setVisibility(View.GONE);
                    frameFragmentVideo.setVisibility(View.GONE);


                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("VideoFragment");
                    mOnTrick = (OnTrickUpdateAdapter) fragment;
                    mOnTrick.OnTrick(getApplicationContext(), getSupportFragmentManager(), keyValue);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void CallFragment() {
        frameFragmentPresentation.removeAllViews();
        frameFragmentVideo.removeAllViews();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment_brochure
                , FragmentBrochuresFile.newInstance(keyValue), "BrochureFragment").commitNow();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment_video
                , FragmentVideo.newInstance(keyValue), "VideoFragment").commitNow();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(FragmentAllPageVideo.newInstance("1", null));
                transaction.commit();
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentTransaction transactionPresentation = manager.beginTransaction();
                transactionPresentation.remove(FragmentAllPagePresentation.newInstance("1"));
                transactionPresentation.commit();
                manager.popBackStack();

                getSupportActionBar().setHomeButtonEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                statusFragmentVideo = false;
                statusFragmentPresentation = false;
                frameFragmentVideo.setVisibility(View.VISIBLE);
                frameFragmentPresentation.setVisibility(View.VISIBLE);
                frameAll.setVisibility(View.GONE);
                CallFragment();
                break;
            case R.id.item_logout:
                getSupportActionBar().setHomeButtonEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(login);
                finish();
                break;
            case R.id.item_saving:
                Intent GoRegistor = new Intent(getApplicationContext(), RegisterActivity.class);
                GoRegistor.putExtra("keyValue", keyValue);
                startActivity(GoRegistor);
                finish();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transactionfragment = fragmentManager.beginTransaction();
                transactionfragment.remove(FragmentAllPageVideo.newInstance("1", null));
                transactionfragment.commit();
                fragmentManager.popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_see_all_presentation:
                Fragment viewPresentation = getSupportFragmentManager().findFragmentById(R.id.allframefragment);
                if (viewPresentation != null) {
                    frameAll.removeAllViews();
                }
                frameFragmentVideo.setVisibility(View.GONE);
                frameFragmentPresentation.setVisibility(View.GONE);
                frameAll.setVisibility(View.VISIBLE);
                statusFragmentPresentation = true;
                statusFragmentVideo = false;


                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.allframefragment
                        , FragmentAllPagePresentation.newInstance(keyValue), "BrochureFragment").addToBackStack(null).commit();
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                break;
            case R.id.tv_see_all_video:
                Fragment viewVideo = getSupportFragmentManager().findFragmentById(R.id.allframefragment);
                if (viewVideo != null) {
                    frameAll.removeAllViews();
                }
                frameFragmentVideo.setVisibility(View.GONE);
                frameFragmentPresentation.setVisibility(View.GONE);
                frameAll.setVisibility(View.VISIBLE);
                statusFragmentPresentation = false;
                statusFragmentVideo = true;

                getSupportFragmentManager().beginTransaction().replace(R.id.allframefragment
                        , FragmentAllPageVideo.newInstance(keyValue, null), "VideoFragment").addToBackStack(null).commit();
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                break;
        }
    }


    @Override
    public void OnClickLoadingVideo(String path) {
        Fragment viewVideo = getSupportFragmentManager().findFragmentById(R.id.allframefragment);
        if (viewVideo != null) {
            frameAll.removeAllViews();
        }
        frameFragmentVideo.setVisibility(View.GONE);
        frameFragmentPresentation.setVisibility(View.GONE);
        frameAll.setVisibility(View.VISIBLE);
        statusFragmentPresentation = false;
        statusFragmentVideo = true;

        getSupportFragmentManager().beginTransaction().replace(R.id.allframefragment
                , FragmentAllPageVideo.newInstance(keyValue, path)).addToBackStack(null).commit();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startHandler();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "OnDestroyWork");
        File deleteFile = new File(Environment.getExternalStorageDirectory() + "/EggBrochureVideo");
        File deleteFileBrochure = new File(Environment.getExternalStorageDirectory() + "/EggBrochure");
        if (deleteFile.exists()) {
            for (File file : deleteFile.listFiles()) {
                String nameTemp = file.getName();
                String deleteName = file.getName();
                String[] stringsSplite = deleteName.split("_");
                if (nameTemp.equals(stringsSplite[0] + "_temp.mp4")) {
                    File localFile = new File(deleteFile, nameTemp);
                    localFile.delete();
                    Log.d("TempFileDelete", nameTemp);
                } else {
                    Log.d("TempFileNotDelete", nameTemp);
                    Log.d("delelteFileNotDelete", stringsSplite[0]);
                }
            }
        }

        if (deleteFileBrochure.exists()) {
            for (File file : deleteFileBrochure.listFiles()) {
                String tempFile = file.getName();
                String nameOriginal = file.getName();
                String[] spliteName = nameOriginal.split("_");
                if (tempFile.equals(spliteName[0] + "_temp.pdf")) {
                    File localFile = new File(deleteFile, tempFile);
                    localFile.delete();
                } else {
                    Log.d("TempFileBrochure", tempFile);
                    Log.d("deleteFileNotDelete", spliteName[0]);
                }
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setmMotionEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                setmMotionEvent(null);
                break;
            case MotionEvent.ACTION_MOVE:
                setmMotionEvent(event);
                break;
        }
        return true;
    }
}
