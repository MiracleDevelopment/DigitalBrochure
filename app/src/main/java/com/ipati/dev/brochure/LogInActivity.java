package com.ipati.dev.brochure;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ipati.dev.brochure.fragment.FragmentLogin;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class LogInActivity extends AppCompatActivity implements FragmentLogin.OnLoginClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    private void PermissionGranted() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Log.d("Permission", String.valueOf(report.getGrantedPermissionResponses()));
                        if (report.areAllPermissionsGranted()) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_login
                                    , FragmentLogin.newInstance("1")).commit();

                        } else {
                            new AlertDialog.Builder(LogInActivity.this)
                                    .setTitle("Please Allow Permission..")
                                    .setMessage("Permission Denied...")
                                    .setPositiveButton("setting", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent =
                                                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                                            intent.setData(Uri.parse("package:"
                                                    + getPackageName()));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionGranted();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    public void OnClickLogin() {
        Intent goContent = new Intent(this, MainActivity.class);
        startActivity(goContent);
        finish();
    }
}
