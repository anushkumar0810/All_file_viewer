package com.example.allfilepicker.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.allfilepicker.Models.Audio;
import com.example.allfilepicker.R;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.allfilepicker.Adapters.AudioAdapter;
import java.util.ArrayList;
import java.util.List;

public class AudioFragment extends Fragment {

    private RecyclerView recyclerViewAudio;
    private AudioAdapter audioAdapter;
    private List<Audio> audioList = new ArrayList<>();

    public AudioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        recyclerViewAudio = view.findViewById(R.id.recyclerViewAudio);

        // Set up RecyclerView with Adapter
        recyclerViewAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        audioAdapter = new AudioAdapter(getContext(), audioList);
        recyclerViewAudio.setAdapter(audioAdapter);

        // Load audio files
        loadAudioFiles();

        return view;
    }

    private void loadAudioFiles() {
        // Clear the audio list to avoid duplication
        audioList.clear();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,   // File path
                MediaStore.Audio.Media.ALBUM_ID  // Album ID for album art
        };

        try (Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                    // Fetch album art URI
                    String albumArt = getAlbumArt(albumId);

                    // Add audio to the list
                    audioList.add(new Audio(title, artist, duration, path, albumArt));

                } while (cursor.moveToNext());
            }
        }

        // Notify adapter of data change
        audioAdapter.notifyDataSetChanged();
    }

    // Fetch the album art using the album ID
    private String getAlbumArt(long albumId) {
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        return Uri.withAppendedPath(albumArtUri, String.valueOf(albumId)).toString();
    }
}
