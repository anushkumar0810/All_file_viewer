package com.example.allfilepicker.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.allfilepicker.Dialog.MediaDialog;
import com.example.allfilepicker.R;

import java.io.File;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final Context context;
    private final List<String> videoList;

    public VideoAdapter(Context context, List<String> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        String videoPath = videoList.get(position);

        // Load video thumbnail using Glide
        Glide.with(context)
                .asBitmap()
                .load(Uri.fromFile(new File(videoPath)))
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.videoThumbnail);

        // Handle click to open MediaDialog for video
        holder.itemView.setOnClickListener(view -> {
            // Show the MediaDialog for the video
            MediaDialog mediaDialog = MediaDialog.newInstance("video", videoPath, null,null);
            mediaDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "MediaDialog");
        });
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
        }
    }
}