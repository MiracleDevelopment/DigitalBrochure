package com.ipati.dev.brochure.adapter;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.ipati.dev.brochure.model.DownloadManagerVideo;
import com.ipati.dev.brochure.OnClickProgressItem;
import com.ipati.dev.brochure.OnItemClickPlayVideo;
import com.ipati.dev.brochure.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ipati on 1/6/2560.
 */

public class RecyclerAdapterVideo extends RecyclerView.Adapter<RecyclerAdapterVideo.MyVideoViewHolder> {
    private ArrayList<String> mlistHeader, mlistImage, mlistDate;
    private FragmentActivity mActivity;
    private OnItemClickPlayVideo mOnItemPlay;

    public RecyclerAdapterVideo(FragmentActivity activity, ArrayList<String> listHeader, ArrayList<String> listImageUrl, ArrayList<String> listDate, OnItemClickPlayVideo mOnItemClick) {
        this.mActivity = activity;
        this.mlistHeader = listHeader;
        this.mlistImage = listImageUrl;
        this.mlistDate = listDate;
        this.mOnItemPlay = mOnItemClick;
    }

    @Override
    public MyVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_custom_layout_video, parent, false);
        return new MyVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyVideoViewHolder holder, final int position) {
        Glide.with(mActivity).load(mlistImage.get(position)).placeholder(R.mipmap.cover_egg).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.mIcon);
        holder.tvHeader.setText(mlistHeader.get(position));

        File file = new File(Environment.getExternalStorageDirectory() + "/EggBrochureVideo");
        if (file.exists()) {
            for (File fileName : file.listFiles()) {
                if (fileName.getName().equals(mlistHeader.get(position) + ".mp4")) {
                    holder.mIcon.setAlpha(1.0f);
                }
            }
        } else {
            file.mkdirs();
        }

        DownloadManagerVideo managerListener = DownloadManagerVideo.getInstance();
        for (String nameFile : managerListener.getHasMap()) {
            if (nameFile.equals(mlistHeader.get(position))) {
                holder.mTaskManager = (FileDownloadTask) managerListener.FirebaseDownloadManager(mlistHeader.get(position) + ".mp4"
                        , mlistHeader.get(position))
                        .addOnSuccessListener(holder.OnSucess)
                        .addOnFailureListener(holder.OnFailure)
                        .addOnProgressListener(holder.OnProgress);
            }
        }

        holder.setmOnClickItemProgress(new OnClickProgressItem() {
            @Override
            public void OnClickProgress(View view, final int position) {
                if (holder.mIcon.getAlpha() == 1.0f) {
                    mOnItemPlay.OnItemClickPlay(mlistHeader.get(position) + ".mp4");
                } else {
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                    holder.tvPercent.setVisibility(View.VISIBLE);
                    holder.mDownloading.setAlpha(0.8f);

                    DownloadManagerVideo managerListener = DownloadManagerVideo.getInstance();

                    holder.mTaskManager = (FileDownloadTask) managerListener.FirebaseDownloadManager(mlistHeader.get(position) + ".mp4"
                            , mlistHeader.get(position))
                            .addOnSuccessListener(holder.OnSucess)
                            .addOnFailureListener(holder.OnFailure)
                            .addOnProgressListener(holder.OnProgress);
                    Toast.makeText(view.getContext(), mlistHeader.get(position), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onViewRecycled(MyVideoViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.mTaskManager != null) {
            holder.mTaskManager.removeOnSuccessListener(holder.OnSucess);
            holder.mTaskManager.removeOnProgressListener(holder.OnProgress);
            holder.mTaskManager.removeOnFailureListener(holder.OnFailure);
            Log.d("ViewRecylced", "RemoveListener");
        }

    }

    @Override
    public int getItemCount() {
        return mlistHeader.size();
    }

    public class MyVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ProgressBar mProgressBar;
        LinearLayout mLinearlayout, itemLayout;
        ImageView mIcon, mDownloading;
        TextView tvHeader, tvDate, tvPercent;
        OnClickProgressItem mOnClickItemProgress;
        FileDownloadTask mTaskManager;

        public MyVideoViewHolder(View itemView) {
            super(itemView);
            itemLayout = (LinearLayout) itemView.findViewById(R.id.item_layout_video);
            mIcon = (ImageView) itemView.findViewById(R.id.im_custom_icon_video);
            tvHeader = (TextView) itemView.findViewById(R.id.tv_custom_name_video);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date_video);
            mLinearlayout = (LinearLayout) itemView.findViewById(R.id.layout_anime_video);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_item_video);
            tvPercent = (TextView) itemView.findViewById(R.id.tv_progress_video);
            mDownloading = (ImageView) itemView.findViewById(R.id.im_background_loading_video);

            mProgressBar.setVisibility(View.GONE);
            tvPercent.setVisibility(View.GONE);
            mDownloading.setAlpha(0.0f);

            itemView.setOnClickListener(this);
        }

        public void setmOnClickItemProgress(OnClickProgressItem mOnClickItemProgress) {
            this.mOnClickItemProgress = mOnClickItemProgress;
        }

        @Override
        public void onClick(View v) {
            mOnClickItemProgress.OnClickProgress(v, getAdapterPosition());
        }

        OnSuccessListener<FileDownloadTask.TaskSnapshot> OnSucess = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                DownloadManagerVideo managerListener = DownloadManagerVideo.getInstance();
                managerListener.getHasMap().remove(mlistHeader.get(getAdapterPosition()));
                File local = new File(Environment.getExternalStorageDirectory() + "/EggBrochureVideo");
                final File file = new File(local, mlistHeader.get(getAdapterPosition()) + "_temp.mp4");
                if (file.exists()) {
                    File renameFile = new File(local, mlistHeader.get(getAdapterPosition()) + ".mp4");
                    file.renameTo(renameFile);
                }
                Log.d("OnSucess", "Work");
                mIcon.setAlpha(1.0f);
                itemLayout.setClickable(true);
                mDownloading.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                tvPercent.setVisibility(View.GONE);
            }
        };

        OnFailureListener OnFailure = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        OnProgressListener<FileDownloadTask.TaskSnapshot> OnProgress = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                mDownloading.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                tvPercent.setVisibility(View.VISIBLE);
                int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                mProgressBar.setProgress(progress);
                itemLayout.setClickable(false);
                tvPercent.setText(String.valueOf(progress) + "%");
                mDownloading.setAlpha(0.8f);
//                Log.d("OnProgress", "Work");
            }
        };


    }


}
