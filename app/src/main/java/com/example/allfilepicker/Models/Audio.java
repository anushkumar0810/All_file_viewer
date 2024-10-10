package com.example.allfilepicker.Models;

public class Audio {
    private String name;
    private String artist;
    private long duration;
    private String path;  // Store the file path
    private String albumArt;  // Add this to store album artwork path or URI

    public Audio(String name, String artist, long duration, String path, String albumArt) {
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.path = path;  // Initialize the path
        this.albumArt = albumArt;  // Initialize album artwork
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public String getPath() {  // Getter for file path
        return path;
    }

    public String getAlbumArt() {  // Getter for album artwork
        return albumArt;
    }
}

