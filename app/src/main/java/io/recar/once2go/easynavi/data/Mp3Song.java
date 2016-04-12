package io.recar.once2go.easynavi.data;

/**
 * Created by once2go on 11.04.16.
 */
public class Mp3Song {

    private long id;
    private String title;
    private String artist;
    private String duration;

    public Mp3Song(long id, String title, String artist, String duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }
}
