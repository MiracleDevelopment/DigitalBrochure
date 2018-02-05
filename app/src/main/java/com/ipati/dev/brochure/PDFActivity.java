package com.ipati.dev.brochure;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class PDFActivity extends AppCompatActivity {
    PDFView mPDFView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        mPDFView = (PDFView) findViewById(R.id.pdfView);
        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("PDF");
        Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            File file = new File(Environment.getExternalStorageDirectory() + "/EggBrochure/" + path);
            mPDFView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .onRender(new OnRenderListener() {
                        @Override
                        public void onInitiallyRendered(int i, float v, float v1) {
                            mPDFView.fitToWidth();
                        }
                    })
                    .load();
        } else {
            File file = new File(Environment.getDataDirectory() + "/EggBrochure/" + path);
            mPDFView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .onRender(new OnRenderListener() {
                        @Override
                        public void onInitiallyRendered(int i, float v, float v1) {
                            mPDFView.fitToWidth();
                        }
                    })
                    .load();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
