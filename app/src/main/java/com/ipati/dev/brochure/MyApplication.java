package com.ipati.dev.brochure;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.ipati.dev.brochure.model.DownloadManagerBrochure;
import com.ipati.dev.brochure.model.DownloadManagerVideo;

/**
 * Created by ipati on 7/6/2560.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DownloadManagerVideo.getInstance().setContext(this);
        DownloadManagerBrochure.getInstance().setmContext(this);
    }
}
