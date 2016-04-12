package io.recar.once2go.easynavi.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.recar.once2go.easynavi.data.Mp3Song;

/**
 * Created by once2go on 11.04.16.
 */
public class MusicSearchHelper {

    public static List<Mp3Song> getMusicList(Context context) {
        List<Mp3Song> mp3SongList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String[] selectionArgsMp3 = new String[]{ mimeType };

        Cursor musicCursor = musicResolver.query(musicUri, null, selectionMimeType, selectionArgsMp3, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String duration = musicCursor.getString(durationColumn);
                int dur = Integer.parseInt(duration != null ? duration : "0");
                long min = TimeUnit.MILLISECONDS.toMinutes(dur);
                long sec = TimeUnit.MILLISECONDS.toSeconds(dur) - TimeUnit.MINUTES.toSeconds(min);
                duration = String.format("%02d:%02d", min, sec);
                mp3SongList.add(new Mp3Song(id, title, artist, duration));
            }
            while (musicCursor.moveToNext());
            return mp3SongList;
        } else {
            return null;
        }
    }
}
