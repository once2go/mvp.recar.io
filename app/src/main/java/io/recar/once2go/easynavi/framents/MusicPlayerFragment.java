package io.recar.once2go.easynavi.framents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import io.recar.once2go.easynavi.R;
import io.recar.once2go.easynavi.interfaces.MusicPlayerControlsListener;
import io.recar.once2go.easynavi.interfaces.MusicPlayerListener;
import io.recar.once2go.easynavi.util.MusicPlayerManager;

/**
 * Created by once2go on 11.04.16.
 */
public class MusicPlayerFragment extends Fragment implements View.OnClickListener, MusicPlayerListener {

    private TextView mSongTitle;
    private TextView mSongArtist;
    private TextView mSongDuration;

    private ImageButton mPlay;
    private ImageButton mPrevious;
    private ImageButton mNext;
    private ImageButton mVolumeUp;
    private ImageButton getmVolumeDown;

    private boolean isPlay;

    private MusicPlayerControlsListener mMusicPlayerControlsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_player_layout, container, false);
        assigneView(view);
        MusicPlayerManager musicPlayerManager = new MusicPlayerManager(getActivity(), this);
        mMusicPlayerControlsListener = musicPlayerManager.getMusicControlsListener();
        return view;
    }

    private void assigneView(View view) {
        mSongTitle = (TextView) view.findViewById(R.id.player_song_title);
        mSongArtist = (TextView) view.findViewById(R.id.player_song_artist);
        mSongDuration = (TextView) view.findViewById(R.id.player_song_duration);

        mPlay = (ImageButton) view.findViewById(R.id.player_song_play);
        mPrevious = (ImageButton) view.findViewById(R.id.player_song_previous);
        mNext = (ImageButton) view.findViewById(R.id.player_song_next);

        mVolumeUp = (ImageButton) view.findViewById(R.id.player_volume_up);
        getmVolumeDown = (ImageButton) view.findViewById(R.id.player_volume_down);


        mPlay.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mVolumeUp.setOnClickListener(this);
        getmVolumeDown.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_song_play:
                if (isPlay){
                    isPlay = false;
                    mPlay.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    isPlay = true;
                    mPlay.setImageResource(android.R.drawable.ic_media_play);
                }
                if (mMusicPlayerControlsListener != null) {
                    mMusicPlayerControlsListener.play();
                }
                break;
            case R.id.player_song_previous:
                if (mMusicPlayerControlsListener != null) {
                    mMusicPlayerControlsListener.playPrevious();
                }
                break;
            case R.id.player_song_next:
                if (mMusicPlayerControlsListener != null) {
                    mMusicPlayerControlsListener.playNext();
                }
                break;
            case R.id.player_volume_up:
                if (mMusicPlayerControlsListener != null) {
                    mMusicPlayerControlsListener.volumeUp();
                }
                break;
            case R.id.player_volume_down:
                if (mMusicPlayerControlsListener != null) {
                    mMusicPlayerControlsListener.volumeDown();
                }
                break;
        }

    }

    @Override
    public void onPause() {
        if (mMusicPlayerControlsListener != null) {
            mMusicPlayerControlsListener.stop();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mMusicPlayerControlsListener != null) {
            mMusicPlayerControlsListener.stop();
        }
        super.onDestroy();
    }

    @Override
    public void playFinished() {

    }

    @Override
    public void playingNow(String title, String artist, String duration) {
        mSongTitle.setText(title);
        mSongArtist.setText(artist);
        mSongDuration.setText(duration);
    }
}
