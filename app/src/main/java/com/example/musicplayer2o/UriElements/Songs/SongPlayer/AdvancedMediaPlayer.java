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
    }

    public boolean isAssetLoaded() { return getDuration() > 0; }


    public void loadAndPlayAsset(Uri song)
    {
        try
        {
            reset();
            setDataSource(song.toString());
            prepareAsync();
            setOnPreparedListener(new MediaPlayer.OnPreparedListener() { public void onPrepared(MediaPlayer mediaPlayer) { mediaPlayer.start(); } });
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

    private int m_pausePointMillisecond;
}
