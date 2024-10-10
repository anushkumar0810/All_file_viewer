package com.example.allfilepicker.Models;

public class Document {
    private String name;
    private long size;
    private String mimeType;

    public Document(String name, long size, String mimeType) {
        this.name = name;
        this.size = size;
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getMimeType() {
        return mimeType;
    }
}
