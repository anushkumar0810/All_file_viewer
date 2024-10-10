package com.example.allfilepicker.Dialog;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.allfilepicker.R;

import java.io.IOException;

public class MediaDialog extends DialogFragment {

    private MediaPlayer mediaPlayer;
    private VideoView videoView; // Added VideoView reference
    private String mediaType;
    private String mediaPath;
    private String audioName;
    private String albumImagePath;

    private SeekBar seekBar; // For audio
    private SeekBar seekBarVideo; // For video
    private TextView runningTime; // For audio
    private TextView runningTimeVideo; // For video
    private TextView fixedTime; // For audio
    private TextView fixedTimeVideo; // For video
    private Handler handler = new Handler(); // To update SeekBar and time display

    // Runnable for updating the SeekBar
    private Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaType.equals("audio") && mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                runningTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 1000); // Update every second
            } else if (mediaType.equals("video") && videoView != null && videoView.isPlaying()) {
                seekBarVideo.setProgress(videoView.getCurrentPosition());
                runningTimeVideo.setText(formatTime(videoView.getCurrentPosition()));
                handler.postDelayed(this, 1000); // Update every second
            }
        }
    };

    public static MediaDialog newInstance(String mediaType, String mediaPath, @Nullable String audioName, @Nullable String albumImagePath) {
        MediaDialog dialog = new MediaDialog();
        Bundle args = new Bundle();
        args.putString("media_type", mediaType);
        args.putString("media_path", mediaPath);
        if (audioName != null) {
            args.putString("audio_name", audioName);
        }
        if (albumImagePath != null) {
            args.putString("album_image_path", albumImagePath);
        }
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.setCancelable(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_media_dialog, container, false);

        if (getArguments() != null) {
            mediaType = getArguments().getString("media_type");
            mediaPath = getArguments().getString("media_path");
            audioName = getArguments().getString("audio_name");
            albumImagePath = getArguments().getString("album_image_path");
        }

        setupDialogViews(view);
        return view;
    }

    private void setupDialogViews(View view) {
        LinearLayout layoutImage = view.findViewById(R.id.layout_image);
        LinearLayout layoutVideo = view.findViewById(R.id.layout_video);
        ConstraintLayout layoutAudio = view.findViewById(R.id.layout_audio);
        ImageView close = view.findViewById(R.id.closeDialog);

        close.setVisibility(View.VISIBLE);

        layoutImage.setVisibility("image".equals(mediaType) ? View.VISIBLE : View.GONE);
        layoutVideo.setVisibility("video".equals(mediaType) ? View.VISIBLE : View.GONE);
        layoutAudio.setVisibility("audio".equals(mediaType) ? View.VISIBLE : View.GONE);

        if ("image".equals(mediaType)) {
            ImageView imageView = view.findViewById(R.id.image_view);
            Glide.with(requireContext()).load(mediaPath).into(imageView);
        } else if ("video".equals(mediaType)) {
            setupVideoPlayer(view, mediaPath); // Set up video player
        } else if ("audio".equals(mediaType)) {
            setupAudioPlayer(view, mediaPath, audioName, albumImagePath); // Set up audio player
        }

        close.setOnClickListener(v -> dismiss());
    }

    private void setupVideoPlayer(View view, String videoPath) {
        videoView = view.findViewById(R.id.video_view);
        runningTimeVideo = view.findViewById(R.id.runningTimevideo);
        fixedTimeVideo = view.findViewById(R.id.fixedTimevideo);
        ImageView playPauseButtonVideo = view.findViewById(R.id.play_pause_video);
        seekBarVideo = view.findViewById(R.id.seekBarVideo);

        // Initialize video view and seek bar
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.setOnPreparedListener(mp -> {
            seekBarVideo.setMax(videoView.getDuration());
            fixedTimeVideo.setText(formatTime(videoView.getDuration()));
            handler.post(updateSeekBarRunnable); // Start updating the SeekBar
        });

        playPauseButtonVideo.setOnClickListener(v -> togglePlayPauseVideo(playPauseButtonVideo));

        // SeekBar listener
        seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBarRunnable); // Stop updating during drag
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress()); // Seek to the new position
                handler.post(updateSeekBarRunnable); // Resume updates
            }
        });
    }

    private void setupAudioPlayer(View view, String audioPath, String audioName, String albumArt) {
        TextView audioFileName = view.findViewById(R.id.audio_file_name);
        ImageView playPauseButton = view.findViewById(R.id.play_pause_audio);
        ImageView audioImage = view.findViewById(R.id.audioImage);
        seekBar = view.findViewById(R.id.seekBar);
        runningTime = view.findViewById(R.id.runningTime);
        fixedTime = view.findViewById(R.id.fixedTime);

        audioFileName.setText(audioName);
        audioFileName.setSelected(true);

        // Load album art using Glide
        if (albumArt != null && !albumArt.isEmpty()) {
            Glide.with(requireContext()).load(albumArt).placeholder(R.drawable.img).error(R.drawable.img).into(audioImage);
        } else {
            audioImage.setImageResource(R.drawable.img);
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mediaPlayer.getDuration());
                fixedTime.setText(formatTime(mediaPlayer.getDuration()));
                handler.post(updateSeekBarRunnable); // Start updating the SeekBar
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        playPauseButton.setOnClickListener(v -> togglePlayPause(playPauseButton));

        // SeekBar listener to update media position
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBarRunnable); // Stop updating during drag
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress()); // Seek to the new position
                handler.post(updateSeekBarRunnable); // Resume updates
            }
        });
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void togglePlayPause(ImageView playPauseButton) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.ic_play);
            handler.removeCallbacks(updateSeekBarRunnable); // Stop updates when paused
        } else if (mediaPlayer != null) {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.ic_pause);
            handler.post(updateSeekBarRunnable); // Resume updates when playing
        }
    }

    private void togglePlayPauseVideo(ImageView playPauseButton) {
        if (videoView.isPlaying()) {
            videoView.pause();
            playPauseButton.setImageResource(R.drawable.ic_play);
            handler.removeCallbacks(updateSeekBarRunnable); // Stop updates when paused
        } else {
            videoView.start();
            playPauseButton.setImageResource(R.drawable.ic_pause);
            handler.post(updateSeekBarRunnable); // Resume updates when playing
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (videoView != null) {
            videoView.stopPlayback();
        }
        handler.removeCallbacks(updateSeekBarRunnable); // Clean up the handler
    }
}
