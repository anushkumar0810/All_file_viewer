package com.example.allfilepicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.media.MediaPlayer;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.allfilepicker.Dialog.MediaDialog;
import com.example.allfilepicker.Models.Audio;
import com.example.allfilepicker.R;

import java.io.IOException;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private Context context;
    private List<Audio> audioList;
    private MediaPlayer mediaPlayer = null;
    private int lastPlayedPosition = -1;  // Track the last played audio

    public AudioAdapter(Context context, List<Audio> audioList) {
        this.context = context;
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_item, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        Audio audio = audioList.get(position);

        // Set truncated song name with marquee
        holder.textViewName.setText(audio.getName());
        holder.textViewName.setSelected(true);  // Enable marquee effect for long names

        holder.textViewArtist.setText(audio.getArtist());
        long minutes = (audio.getDuration() / 1000) / 60;
        long seconds = (audio.getDuration() / 1000) % 60;
        holder.textViewDuration.setText(String.format("%d:%02d", minutes, seconds));

        // Load album art into the audioImage view using Glide
        String albumArt = audio.getAlbumArt();
        Glide.with(context)
                .load(albumArt)
                .placeholder(R.drawable.img)  // Fallback if album art fails to load
                .error(R.drawable.img)        // Fallback if the URI is invalid
                .into(holder.audioImage);



        // Set play/pause button click
        holder.playPause.setOnClickListener(view -> handlePlayPause(holder, position));

        // Handle click to open MediaDialog for audio
        holder.itemView.setOnClickListener(view -> {
            // Show the MediaDialog for the audio
            MediaDialog mediaDialog = MediaDialog.newInstance("audio", audio.getPath(), audio.getName(), audio.getAlbumArt());
            mediaDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "AudioDialog");
        });
    }

    // Handle play/pause actions
    private void handlePlayPause(AudioViewHolder holder, int position) {
        Audio audio = audioList.get(position);

        if (mediaPlayer != null && lastPlayedPosition == position) {
            // Toggle between play and pause
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                holder.playPause.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                holder.playPause.setImageResource(R.drawable.ic_pause);
            }
        } else {
            // Release the previous mediaPlayer and reset the play button of last played item
            if (mediaPlayer != null) {
                releaseMediaPlayer();
                notifyItemChanged(lastPlayedPosition); // Reset last played item
            }

            // Initialize and play new audio
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audio.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                holder.playPause.setImageResource(R.drawable.ic_pause);
                lastPlayedPosition = position;
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Handle audio completion
            mediaPlayer.setOnCompletionListener(mp -> {
                holder.playPause.setImageResource(R.drawable.ic_play);
                releaseMediaPlayer();
                lastPlayedPosition = -1;
            });
        }
    }

    // Release MediaPlayer resources
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    @Override
    public void onViewRecycled(@NonNull AudioViewHolder holder) {
        super.onViewRecycled(holder);
        if (mediaPlayer != null && lastPlayedPosition == holder.getAdapterPosition()) {
            releaseMediaPlayer();  // Release media player if the view is recycled
        }
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewArtist, textViewDuration;
        ImageView playPause, audioImage;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewArtist = itemView.findViewById(R.id.textViewArtist);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            playPause = itemView.findViewById(R.id.play_pause);
            audioImage = itemView.findViewById(R.id.audioImage);
        }
    }
}
