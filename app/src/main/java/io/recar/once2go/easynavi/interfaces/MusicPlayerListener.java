package io.recar.once2go.easynavi.interfaces;

/**
 * Created by once2go on 11.04.16.
 */
public interface MusicPlayerListener {

    void playFinished();

    void playingNow(String title, String artist, String duration);
}
