package com.ipati.dev.brochure.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.Toast;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ipati on 8/6/2560.
 */

public class DownloadManagerBrochure {
    private static DownloadManagerBrochure instance = null;
    HashMap<String, FileDownloadTask> mHasMap = new HashMap<>();
    private Context mContext;
    private File local, file;

    public static DownloadManagerBrochure getInstance() {
        if (instance == null) {
            instance = new DownloadManagerBrochure();
        }
        return instance;
    }

    private DownloadManagerBrochure() {

    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public FileDownloadTask FirebaseDownloadManager(final String name, final String nameOriginal) {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        StorageReference Ref = FirebaseStorage.getInstance().getReference();
        StorageReference mRef = Ref.child("BrochureFile").child(name);
        if (isSDPresent) {
            local = new File(Environment.getExternalStorageDirectory() + "/EggBrochure");
            file = new File(local, nameOriginal + "_temp.pdf");
            if (!local.exists()) {
                local.mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (mHasMap.get(nameOriginal) != null) {
                SpaceFree(mContext, local, mHasMap.get(nameOriginal));
                return mHasMap.get(nameOriginal);
            } else {
                mHasMap.put(nameOriginal, mRef.getFile(file));
            }
        } else {
            local = new File(Environment.getDataDirectory() + "/EggBrochure");
            file = new File(local, nameOriginal + "_temp.pdf");
            if (!local.exists()) {
                local.mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (mHasMap.get(nameOriginal) != null) {
                SpaceFree(mContext, local, mHasMap.get(nameOriginal));
                return mHasMap.get(nameOriginal);
            } else {
                mHasMap.put(nameOriginal, mRef.getFile(file));
            }
        }
        return mRef.getFile(file);

    }

    private void SpaceFree(Context mContext, File local, final FileDownloadTask taskDownload) {
        MemoryState mem = new MemoryState();
        long freeSpace = mem.FreeSpaceInt(local.getPath());

        if (freeSpace >= 0 && freeSpace <= 100) {
            taskDownload.pause();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Alert Memory less 100 MB");
            builder.setCancelable(false);
            builder.setMessage("Memory less 100 MB");
            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    taskDownload.cancel();
                    dialog.cancel();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        } else if (freeSpace > 100 && freeSpace <= 500) {
            Toast.makeText(mContext, String.valueOf(freeSpace) + "MB", Toast.LENGTH_SHORT).show();
        }
    }

    public Set<String> getmHasMap() {
        return mHasMap.keySet();
    }
}
