package com.ipati.dev.brochure.adapter;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.ipati.dev.brochure.OnClickProgressItem;
import com.ipati.dev.brochure.PDFActivity;
import com.ipati.dev.brochure.R;
import com.ipati.dev.brochure.model.DownloadManagerBrochure;


import java.io.File;
import java.util.ArrayList;

/**
 * Created by ipati on 1/6/2560.
 */

public class RecyclerAdapterBrochure extends RecyclerView.Adapter<RecyclerAdapterBrochure.MyViewHolder> {
    private ArrayList<String> mlistHeader, mlistImageUrl, mlistDate;
    private Activity mActivity;

    public RecyclerAdapterBrochure(FragmentActivity activity, ArrayList<String> listHeader, ArrayList<String> listImageUrl
            , ArrayList<String> listDate) {
        this.mActivity = activity;
        this.mlistHeader = listHeader;
        this.mlistImageUrl = listImageUrl;
        this.mlistDate = listDate;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_custom_layout_presentation, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Glide.with(mActivity).load(mlistImageUrl.get(position)).placeholder(R.mipmap.cover).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imIcon);
        holder.mText.setText(mlistHeader.get(position));

        File file = new File(Environment.getExternalStorageDirectory() + "/EggBrochure");
        if (file.exists()) {
            for (File nameFile : file.listFiles()) {
                if (nameFile.getName().equals(mlistHeader.get(position) + ".pdf")) {
                    holder.imIcon.setAlpha(1.0f);
                }
            }
        } else {
            file.mkdirs();
        }

        DownloadManagerBrochure loadManager = DownloadManagerBrochure.getInstance();
        for (String nameKey : loadManager.getmHasMap()) {
            if (nameKey.equals(mlistHeader.get(position))) {
                holder.mDownloadTask = (FileDownloadTask) loadManager.FirebaseDownloadManager(mlistHeader.get(position) + ".pdf", mlistHeader.get(position))
                        .addOnSuccessListener(holder.OnSucess)
                        .addOnFailureListener(holder.OnFailure)
                        .addOnProgressListener(holder.OnProgress);
            }
        }


        holder.setmOnClickProgressItem(new OnClickProgressItem() {
            @Override
            public void OnClickProgress(View view, int position) {
                if (holder.imIcon.getAlpha() == 1.0) {
                    Intent ReaderPDF = new Intent(mActivity, PDFActivity.class);
                    ReaderPDF.putExtra("PDF", mlistHeader.get(position) + ".pdf");
                    mActivity.startActivity(ReaderPDF);
                } else {
                    holder.imLoading.setAlpha(0.8f);
                    holder.imIcon.setVisibility(View.VISIBLE);
                    holder.mProgressText.setVisibility(View.VISIBLE);
                    holder.mProgress.setVisibility(View.VISIBLE);

                    DownloadManagerBrochure mDownloadManager = DownloadManagerBrochure.getInstance();
                    holder.mDownloadTask = (FileDownloadTask) mDownloadManager.FirebaseDownloadManager(mlistHeader.get(position) + ".pdf", mlistHeader.get(position))
                            .addOnSuccessListener(holder.OnSucess)
                            .addOnProgressListener(holder.OnProgress)
                            .addOnFailureListener(holder.OnFailure);
                    Toast.makeText(view.getContext(), String.valueOf(mlistHeader.get(position)), Toast.LENGTH_SHORT).show();
                }
//
            }
        });
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.mDownloadTask != null) {
            holder.mDownloadTask.removeOnSuccessListener(holder.OnSucess);
            holder.mDownloadTask.removeOnProgressListener(holder.OnProgress);
            holder.mDownloadTask.removeOnFailureListener(holder.OnFailure);
            Log.d("Brochure", "Recycled");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mlistHeader.get(position).equals(mlistHeader.get(position))) {
            return position;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mlistImageUrl.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout mRelativeLayout;
        ProgressBar mProgress;
        LinearLayout mLinearlayout, mView;
        ImageView imIcon, imLoading;
        TextView mText, mDate, mProgressText;
        OnClickProgressItem mOnClickProgressItem;
        FileDownloadTask mDownloadTask;

        public MyViewHolder(View itemView) {
            super(itemView);
            mLinearlayout = (LinearLayout) itemView.findViewById(R.id.layout_anime_presentation);
            imIcon = (ImageView) itemView.findViewById(R.id.im_custom_icon);
            mText = (TextView) itemView.findViewById(R.id.tv_custom_name);
            mDate = (TextView) itemView.findViewById(R.id.tv_date);
            mProgress = (ProgressBar) itemView.findViewById(R.id.progress_item);
            mProgressText = (TextView) itemView.findViewById(R.id.tv_progress);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_relative);
            imLoading = (ImageView) itemView.findViewById(R.id.im_background_loading);
            mView = (LinearLayout) itemView.findViewById(R.id.view_item_presentation);


            mProgress.setVisibility(View.GONE);
            mProgressText.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        public void setmOnClickProgressItem(OnClickProgressItem mOnClickProgressItem) {
            this.mOnClickProgressItem = mOnClickProgressItem;
        }

        @Override
        public void onClick(View v) {
            mOnClickProgressItem.OnClickProgress(v, getAdapterPosition());
        }

        OnSuccessListener<FileDownloadTask.TaskSnapshot> OnSucess = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                DownloadManagerBrochure manager = DownloadManagerBrochure.getInstance();
                if (getAdapterPosition() <= mlistHeader.size()) {
                    manager.getmHasMap().remove(mlistHeader.get(getAdapterPosition()));
                }
                File locationFile = new File(Environment.getExternalStorageDirectory() + "/EggBrochure");
                File file = new File(locationFile, mlistHeader.get(getAdapterPosition()) + "_temp.pdf");
                if (file.exists()) {
                    File reNameFile = new File(locationFile, mlistHeader.get(getAdapterPosition()) + ".pdf");
                    file.renameTo(reNameFile);
                }
                imIcon.setAlpha(1.0f);
                mView.setClickable(true);
                mProgress.setVisibility(View.GONE);
                mProgressText.setVisibility(View.GONE);
                imLoading.setVisibility(View.GONE);
            }
        };

        OnFailureListener OnFailure = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mView.setClickable(true);
                mProgress.setVisibility(View.GONE);
                mProgressText.setVisibility(View.GONE);
                imLoading.setVisibility(View.GONE);
                Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        OnProgressListener<FileDownloadTask.TaskSnapshot> OnProgress = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                imLoading.setVisibility(View.VISIBLE);
                imLoading.setAlpha(0.8f);
                mProgressText.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.VISIBLE);
                mView.setClickable(false);
                int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                mProgress.setProgress(progress);
                mProgressText.setText(String.valueOf(progress) + "%");
            }
        };
    }
}
