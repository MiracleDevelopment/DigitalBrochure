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
 * Created by ipati on 14/6/2560.
 */

public class DownloadManagerVideo {
    private static DownloadManagerVideo instance = null;
    HashMap<String, FileDownloadTask> hasMap = new HashMap<>();
    Context mContext;
    private File local, file;

    public static DownloadManagerVideo getInstance() {
        if (instance == null) {
            instance = new DownloadManagerVideo();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public FileDownloadTask FirebaseDownloadManager(final String name
            , final String nameOriginal) {
        StorageReference Ref = FirebaseStorage.getInstance().getReference();
        StorageReference mRef = Ref.child("VideoFile").child(name);
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            local = new File(Environment.getExternalStorageDirectory() + "/EggBrochureVideo");
            file = new File(local, nameOriginal + "_temp.mp4");
            if (!local.exists()) {
                local.mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            if (hasMap.get(nameOriginal) != null) {
                SpaceFree(mContext, hasMap.get(nameOriginal), local);
                return hasMap.get(nameOriginal);
            } else {
                hasMap.put(nameOriginal, mRef.getFile(file));

            }
        }else{
            local = new File(Environment.getDataDirectory() + "/EggBrochureVideo");
            file = new File(local, nameOriginal + "_temp.mp4");
            if (!local.exists()) {
                local.mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            if (hasMap.get(nameOriginal) != null) {
                SpaceFree(mContext, hasMap.get(nameOriginal), local);
                return hasMap.get(nameOriginal);
            } else {
                hasMap.put(nameOriginal, mRef.getFile(file));

            }
        }
        return hasMap.get(nameOriginal);
    }


    private void SpaceFree(Context context, final FileDownloadTask taskDownload, File local) {
        MemoryState mem = new MemoryState();
        long freeSpace = mem.FreeSpaceInt(local.getPath());
        if (freeSpace >= 0 && freeSpace <= 100) {
            taskDownload.pause();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
            Toast.makeText(context, String.valueOf(freeSpace) + "MB", Toast.LENGTH_SHORT).show();
        }
    }

    public Set<String> getHasMap() {
        return hasMap.keySet();
    }


}
