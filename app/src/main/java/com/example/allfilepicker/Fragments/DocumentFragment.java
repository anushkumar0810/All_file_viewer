package com.example.allfilepicker.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.allfilepicker.Adapters.DocumentAdapter;
import com.example.allfilepicker.Models.Document;
import com.example.allfilepicker.R;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.recyclerview.widget.LinearLayoutManager;

public class DocumentFragment extends Fragment {

    private RecyclerView recyclerViewDocuments;
    private DocumentAdapter documentAdapter;
    private List<Document> documentList = new ArrayList<>();

    public DocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document, container, false);
        recyclerViewDocuments = view.findViewById(R.id.recyclerViewDocuments);

        // Set up RecyclerView
        recyclerViewDocuments.setLayoutManager(new LinearLayoutManager(getContext()));
        documentAdapter = new DocumentAdapter(getContext(), documentList);
        recyclerViewDocuments.setAdapter(documentAdapter);

        loadDocuments();

        return view;
    }

    private void loadDocuments() {
        // Clear the document list before loading new documents
        documentList.clear();

        // Load documents from the device
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE
        };

        // Query to get all files
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));

                // Check if the file is a document (exclude images and audio)
                if (isDocumentFile(mimeType)) {
                    documentList.add(new Document(name, size, mimeType));
                }
            }
            cursor.close();
        }

        // Notify the adapter that the data has changed
        documentAdapter.notifyDataSetChanged();
    }

    // Helper method to check if the file is a document
    private boolean isDocumentFile(String mimeType) {
        return mimeType != null && !mimeType.startsWith("image/") && !mimeType.startsWith("audio/");
    }


}