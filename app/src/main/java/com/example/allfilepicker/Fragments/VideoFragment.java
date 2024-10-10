package com.example.allfilepicker.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.allfilepicker.R;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.allfilepicker.Adapters.VideoAdapter;
import java.util.ArrayList;
import java.util.List;


public class VideoFragment extends Fragment {

    private static final int REQUEST_PERMISSIONS = 101;
    private RecyclerView recyclerViewVideos;
    private VideoAdapter videoAdapter;
    private List<String> videoList = new ArrayList<>();

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        recyclerViewVideos = view.findViewById(R.id.recyclerViewVideos);

        // Set up RecyclerView
        recyclerViewVideos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        videoAdapter = new VideoAdapter(getContext(), videoList);
        recyclerViewVideos.setAdapter(videoAdapter);

        // Check and request permissions based on the Android version
        checkPermissions();

        return view;
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            // For Android 14 and above
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                loadVideos();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, REQUEST_PERMISSIONS);
            }
        }else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            // For Android 13
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED) {
                loadVideos();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_PERMISSIONS);
            }
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            // For Android 6 to 12
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                loadVideos();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
        } else {
            // For Android versions below Marshmallow
            loadVideos();
        }
    }

    private void loadVideos() {
        // Clear the video list before loading new videos
        videoList.clear();

        // Load videos from the device
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                videoList.add(path);
            }
            cursor.close();
        }

        // Notify the adapter that the data has changed
        videoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideos();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}