package com.example.allfilepicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allfilepicker.Models.Document;
import com.example.allfilepicker.R;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private final Context context;
    private final List<Document> documentList;

    public DocumentAdapter(Context context, List<Document> documentList) {
        this.context = context;
        this.documentList = documentList;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documentList.get(position);

        holder.documentName.setText(document.getName());
        holder.documentSize.setText(formatFileSize(document.getSize()));
        holder.documentType.setText(document.getMimeType());
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView documentName;
        TextView documentSize;
        TextView documentType;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            documentName = itemView.findViewById(R.id.documentName);
            documentSize = itemView.findViewById(R.id.documentSize);
            documentType = itemView.findViewById(R.id.documentType);
        }
    }

    // Utility method to format file size
    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int idx = (int) (Math.log10(size) / 3);
        return String.format("%.2f %s", size / Math.pow(1024, idx), units[idx]);
    }
}
