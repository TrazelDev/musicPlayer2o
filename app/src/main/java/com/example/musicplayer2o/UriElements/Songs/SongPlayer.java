package com.example.musicplayer2o.UriElements.Songs;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.example.musicplayer2o.R;
import com.example.musicplayer2o.UriElements.Images.ImageUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SongPlayer
{
    public interface OnSongMaxDurationChanged { public void execute(String formattedDuration); }
    public interface OnSongImageChanged { public void execute(Uri imgUri); }
    public SongPlayer(Playlist playlist)
    {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        m_mediaPlayer = new MediaPlayer();
        m_mediaPlayer.setAudioAttributes(audioAttributes);

        m_playlist = playlist;
        m_currSong = m_playlist.getNewSong();
    }

    public void forceUpdate()
    {
        m_currSong.setOnRetrieveImageAction(imgUri -> m_songImageSetter.execute(imgUri));
        if(m_mediaPlayer.isPlaying()) m_songDurationSetter.execute(convertSongSecondsWithFormat(getSongDurationSecondsWhileSongActive()));
        else m_currSong.setOnRetrieveSongAction(songUri -> { setActionsWithSongDurationWhileSongOff(songUri, m_songDurationSetter); });
    }
    public void setupSongPictureUpdater(OnSongImageChanged songImageSetter)
    {
        m_currSong.setOnRetrieveImageAction(imgUri -> songImageSetter.execute(imgUri));
        m_songImageSetter = songImageSetter;
    }
    public boolean isSongPlaying() { return m_mediaPlayer.isPlaying(); }
    public int getSongDurationPlayedSeconds() { return m_mediaPlayer.getCurrentPosition() / 1000; }
    public int getSongDurationSecondsWhileSongActive()
    {
        if(m_mediaPlayer != null) return m_mediaPlayer.getDuration() / 1000;
        return 0;
    }
    public int getSongPercentagePassed()
    {
        return (int)(((double)getSongDurationPlayedSeconds() / getSongDurationSecondsWhileSongActive()) * 100);
    }
    public void setupNewSongPlayingTimePoint(int newTimePointPercentage)
    {
        int songMilliSecondsPassed = convertSongPercentageToMillisecondsPassed(newTimePointPercentage);
        boolean wasSongPlaying = m_mediaPlayer.isPlaying();

        m_mediaPlayer.pause();
        m_mediaPlayer.seekTo(songMilliSecondsPassed);
        if (wasSongPlaying) m_mediaPlayer.start();
    }
    public void setupSongsMaxDurationUpdater(OnSongMaxDurationChanged songDurationSetter)
    {
        m_songDurationSetter = songDurationSetter;
        m_currSong.setOnRetrieveSongAction(songUri -> { setActionsWithSongDurationWhileSongOff(songUri, songDurationSetter); });
    }
    public void playOrPause()
    {
        if(isSongPlayedForFirstTime())
        {
            m_currSong.setOnRetrieveSongAction(songUri -> startPlayingSong(songUri));
            return;
        }

        if(isSongUnPaused())
        {
            m_mediaPlayer.seekTo(m_positionSongPausedOn);
            m_mediaPlayer.start();
            return;
        }

        m_positionSongPausedOn = m_mediaPlayer.getCurrentPosition();
        m_mediaPlayer.pause();
    }
    // format - minutes:seconds
    public String convertSongSecondsWithFormat(int seconds)
    {
        return String.format("%01d:%02d", (seconds / 60) % 60, seconds % 60);
    }

    private boolean isSongPlayedForFirstTime() { return m_positionSongPausedOn == 0 && !m_mediaPlayer.isPlaying(); }
    private boolean isSongUnPaused() { return !m_mediaPlayer.isPlaying(); }
    private int convertSongPercentageToMillisecondsPassed(int songPercentage)
    {
        int songDurationPassedSeconds = (int)(((float)songPercentage / 100) * getSongDurationSecondsWhileSongActive());
        return songDurationPassedSeconds * 1000;
    }
    private void startPlayingSong(Uri songUri)
    {
        try
            {
                m_mediaPlayer.reset();
                m_mediaPlayer.setDataSource(songUri.toString());
                m_mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp)
                    {
                        mp.start();
                        setupActionsOnSongEnded();
                    }
                });
                m_mediaPlayer.prepareAsync();
        }
        catch (Exception e) { Log.e("Song Player", "Error setting data source: " + e.getMessage()); }
    }
    public void onSongEndedActions()
    {
        m_currSong = m_playlist.getNewSong();
        m_positionSongPausedOn = 0;
        m_currSong.setOnRetrieveSongAction(songUri -> setActionsWithSongDurationWhileSongOff(songUri, m_songDurationSetter));
        m_currSong.setOnRetrieveImageAction(imgUri -> m_songImageSetter.execute(imgUri));


        Handler delayHandler = new Handler();
        Runnable delayToNotOverrideSongDurationRead = new Runnable()
        {
            @Override
            public void run() { playOrPause(); }
        };
        delayHandler.postDelayed(delayToNotOverrideSongDurationRead, 1000);
    }
    private void setActionsWithSongDurationWhileSongOff(Uri songUri, OnSongMaxDurationChanged songDurationSetter)
    {
        try
        {
            m_mediaPlayer.reset();
            m_mediaPlayer.setDataSource(songUri.toString());
            m_mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    mp.start();

                    int songDurationSeconds = getSongDurationSecondsWhileSongActive();
                    String formattedSongDuration = convertSongSecondsWithFormat(songDurationSeconds);
                    songDurationSetter.execute(formattedSongDuration);

                    mp.pause();
                }
            });
            m_mediaPlayer.prepareAsync();
        }
        catch (Exception e) { }
    }

    private void setupActionsOnSongEnded()
    {
        m_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                m_mediaPlayer = mediaPlayer;
                onSongEndedActions();
            }
        });
    }
    private MediaPlayer m_mediaPlayer;
    private Playlist m_playlist;
    private int m_positionSongPausedOn = 0;
    private Song m_currSong;
    private OnSongMaxDurationChanged m_songDurationSetter;
    private OnSongImageChanged m_songImageSetter;

}
