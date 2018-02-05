package com.ipati.dev.brochure;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.Toast;


public abstract class BaseApplication extends AppCompatActivity {
    Handler handler;
    MotionEvent mMotionEvent;

    public void BoardReciver(MotionEvent mMotionEvent) {
        if (mMotionEvent == null) {
            startHandler();
            handler.removeMessages(0);
            Intent videoBreake = new Intent(this, VideoBreaker.class);
            startActivity(videoBreake);
//            Toast.makeText(getApplicationContext(), "Don't Touch Event", Toast.LENGTH_SHORT).show();
        } else {
            startHandler();
//            Toast.makeText(getApplicationContext(), "Thread Destroy", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(0);
    }

    public Handler startHandler() {
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                BoardReciver(getmMotionEvent());
            }
        };

        handler.sendEmptyMessageDelayed(0, 300000);
        return handler;
    }


    public void setmMotionEvent(MotionEvent mMotionEvent) {
        this.mMotionEvent = mMotionEvent;
    }

    public MotionEvent getmMotionEvent() {
        return mMotionEvent;
    }

}
