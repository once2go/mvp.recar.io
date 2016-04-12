package io.recar.once2go.easynavi.util;

import android.content.ContentUris;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import io.recar.once2go.easynavi.data.Mp3Song;
import io.recar.once2go.easynavi.interfaces.MusicPlayerControlsListener;
import io.recar.once2go.easynavi.interfaces.MusicPlayerListener;

/**
 * Created by once2go on 11.04.16.
 */
public class MusicPlayerManager implements MusicPlayerControlsListener {

    private Context mContext;
    private List<Mp3Song> mMp3SongList;
    private MusicPlayerListener mMusicPlayerListener;
    private MediaPlayer mMediaPlayer;
    private int curentSong;

    float mInitVolume = 0.5f;

    public MusicPlayerManager(Context context, @NonNull MusicPlayerListener musicPlayerListener) {
        mContext = context;
        mMp3SongList = MusicSearchHelper.getMusicList(context);
        mMusicPlayerListener = musicPlayerListener;
    }


    @Override
    public void play() {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            releaseMediaPlayer();
        } else {
            playMusic(curentSong);
        }
    }

    @Override
    public void playPrevious() {
        releaseMediaPlayer();
        curentSong--;
        if (curentSong < 0) {
            curentSong = 0;
        }
        playMusic(curentSong);
    }

    @Override
    public void playNext() {
        releaseMediaPlayer();
        curentSong++;
        if (curentSong > mMp3SongList.size() - 1) {
            curentSong = 0;
        }
        playMusic(curentSong);
    }

    @Override
    public void pause() {

    }

    @Override
    public void volumeUp() {
        if (mMediaPlayer!=null && mInitVolume < 1.0f) {
            mInitVolume += 0.1f;
            mMediaPlayer.setVolume(mInitVolume, mInitVolume);
        }
    }

    @Override
    public void volumeDown() {
        if (mMediaPlayer!=null && mInitVolume > 0) {
            mInitVolume -= 0.1f;
            mMediaPlayer.setVolume(mInitVolume, mInitVolume);
        }
    }

    @Override
    public void stop() {
        releaseMediaPlayer();
    }

    public MusicPlayerControlsListener getMusicControlsListener() {
        return this;
    }

    private void playMusic(int id) {
        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mMp3SongList.get(id).getId());
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(mInitVolume, mInitVolume);
        try {
            mMediaPlayer.setDataSource(mContext, contentUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                String artist = mMp3SongList.get(curentSong).getArtist();
                String title = mMp3SongList.get(curentSong).getTitle();
                String duration = mMp3SongList.get(curentSong).getDuration();
                mMusicPlayerListener.playingNow(title, artist, duration);
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                curentSong++;
                releaseMediaPlayer();
                if (curentSong == mMp3SongList.size() - 1) {
                    mMusicPlayerListener.playFinished();
                } else {
                    playMusic(curentSong);
                }
            }
        });
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
