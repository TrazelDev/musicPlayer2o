package com.example.musicplayer2o.UriElements.Songs.SongPlayer;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

class AdvancedMediaPlayer extends MediaPlayer
{
    AdvancedMediaPlayer()
    {
        super();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        setAudioAttributes(audioAttributes);
        m_pausePointMillisecond = 0;
    }

    public boolean isAssetLoaded() { return getDuration() > 0; }
    public void loadAsset(Uri song, MediaPlayer.OnPreparedListener actionsWithPreparedMediaPlayer)
    {
        try
        {
            reset();
            setDataSource(song.toString());
            prepareAsync();
            setOnPreparedListener(actionsWithPreparedMediaPlayer);
        }
        catch (IOException e) { e.printStackTrace(); }
    }
    public void pauseAsset()
    {
        m_pausePointMillisecond = getCurrentPosition();
        pause();
    }
    public void resumePlaying()
    {
        seekTo(m_pausePointMillisecond);
        start();
    }

    public int getAssetMaxMilliseconds() { return getDuration(); }
    public int getAssetElapsedMilliseconds()
    {
        if(isPlaying()) return getCurrentPosition();
        return m_pausePointMillisecond;
    }


    private int m_pausePointMillisecond;
}
