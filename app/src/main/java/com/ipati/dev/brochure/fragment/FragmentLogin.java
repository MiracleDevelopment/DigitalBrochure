package com.ipati.dev.brochure.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.ipati.dev.brochure.R;

import java.io.File;
import java.io.IOException;

public class FragmentLogin extends Fragment implements View.OnClickListener {
    private final static String KEY = "Login";
    private StorageReference Ref, mRef;
    EditText mAccount;
    Button mLoginBt;
    OnLoginClickListener mOnLoginClick;
    ProgressDialog mProgressDialog;

    public static FragmentLogin newInstance(String value) {
        FragmentLogin fragmentLogin = new FragmentLogin();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, value);
        fragmentLogin.setArguments(bundle);
        return fragmentLogin;
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = mInflater.inflate(R.layout.activity_fragment_login, container, false);
        initInstance(view);
        return view;
    }

    private void initInstance(View view) {
        mAccount = (EditText) view.findViewById(R.id.ed_account);
        mLoginBt = (Button) view.findViewById(R.id.bt_login);
        Ref = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(getContext());
        mLoginBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                mOnLoginClick = (OnLoginClickListener) getActivity();
                String accountId = String.valueOf(mAccount.getText().toString());
                if (accountId.equals("1234")) {
                    DownloadBackgroundFile();

                } else {
                    Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void DownloadBackgroundFile() {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            final File location = new File(Environment.getExternalStorageDirectory() + "/EggVideoFileBackground");
            final File file = new File(location, "backgroundVideo_temp.mp4");
            final File stateFile = new File(location, "backgroundVideo.mp4");
            if (!stateFile.exists()) {
                mRef = Ref.child("VideoFile").child("backgroundVideo.mp4");

                if (!location.exists()) {
                    location.mkdirs();

                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                final FileDownloadTask taskDownload = mRef.getFile(file);
                taskDownload.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        File nameFile = new File(location, "backgroundVideo.mp4");
                        file.renameTo(nameFile);
                        mProgressDialog.cancel();
                        mOnLoginClick.OnClickLogin();
                        Toast.makeText(getContext(), "Login", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        AlertProgressDialog(progress);
                    }
                });
            } else {
                mOnLoginClick.OnClickLogin();
                Toast.makeText(getContext(), "Login", Toast.LENGTH_SHORT).show();
            }
        } else {
            final File location = new File(Environment.getDataDirectory() + "/EggVideoFileBackground");
            final File file = new File(location, "backgroundVideo_temp.mp4");
            final File stateFile = new File(location, "backgroundVideo.mp4");
            if (!stateFile.exists()) {
                mRef = Ref.child("VideoFile").child("backgroundVideo.mp4");

                if (!location.exists()) {
                    location.mkdirs();

                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                final FileDownloadTask taskDownload = mRef.getFile(file);
                taskDownload.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        File nameFile = new File(location, "backgroundVideo.mp4");
                        file.renameTo(nameFile);
                        mProgressDialog.cancel();
                        mOnLoginClick.OnClickLogin();
                        Toast.makeText(getContext(), "Login", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        AlertProgressDialog(progress);
                    }
                });
            } else {
                mOnLoginClick.OnClickLogin();
                Toast.makeText(getContext(), "Login", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void AlertProgressDialog(int Progress) {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("Setting....");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(Progress);
        mProgressDialog.show();
    }

    public interface OnLoginClickListener {
        void OnClickLogin();
    }
}
