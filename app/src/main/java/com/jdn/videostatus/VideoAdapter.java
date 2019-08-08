package com.jdn.videostatus;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private ArrayList<VideoItem> dataSet;
    OnDownloadClickListener listener;

    public interface OnDownloadClickListener{
        public void onDownloadClick(VideoItem item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageViewIcon;
        AVLoadingIndicatorView progressVIew;
        LinearLayout llDownload;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = itemView.findViewById(R.id.textViewName);
            this.imageViewIcon = itemView.findViewById(R.id.imageView);
            this.progressVIew = itemView.findViewById(R.id.progressVIew);
            this.llDownload = itemView.findViewById(R.id.llDownload);
        }
    }

    public VideoAdapter(ArrayList<VideoItem> data, OnDownloadClickListener listener) {
        this.dataSet = data;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        final VideoItem item = dataSet.get(listPosition);

        TextView textViewName = holder.textViewName;
        final ImageView imageView = holder.imageViewIcon;
        final AVLoadingIndicatorView progressVIew = holder.progressVIew;
        LinearLayout llDownload = holder.llDownload;
        progressVIew.setVisibility(View.VISIBLE);
        progressVIew.setIndicatorColor(ContextCompat.getColor(imageView.getContext(), R.color.colorPrimary));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(imageView.getContext(), VideoViewActivity.class);
                intent.putExtra(Constants.VIDEO, item.location + item.name);
                imageView.getContext().startActivity(intent);
            }
        });

        llDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDownloadClick(item);
            }
        });

        textViewName.setText(item.name.replace(".mp4", ""));
        Glide.with(imageView.getContext()).load(item.location + item.name.substring(0, item.name.lastIndexOf(".")) + ".jpg").listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressVIew.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressVIew.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}