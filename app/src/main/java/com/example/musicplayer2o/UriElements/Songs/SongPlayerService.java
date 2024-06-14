package com.example.musicplayer2o.UriElements.Songs;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;

public class SongPlayerService extends Service
{
    // Binder ( Used to access the service in activities and fragments ):
    @Override
    public IBinder onBind(Intent intent) { return binder; }
    private final IBinder binder = new LocalBinder();
    public class LocalBinder extends Binder { public SongPlayerService getService() { return SongPlayerService.this; } }
    // Initial setup:
    public void onCreate()
    {
        super.onCreate();
        m_mediaPlayer = setupMediaPlayer();
        setSongPercentageAndTimePassed();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }





    // Song updating:
    public void uploadPlayList(Playlist playlist)
    {
        m_playlist = playlist;
        m_currSong = playlist.getNewSong();
    }
    public void playOrPause()
    {
        for (SongPlayerServiceUiCallbacks uiCallbacks : m_uiCallbacksList)
            uiCallbacks.setIsSongPlaying(!m_mediaPlayer.isPlaying()); // ! - used because we switch the state later in the function

        if(isCurrSongPlayedForFirstTime())
        {
            m_currSong.setOnRetrieveSongAction(songUri ->
            {
                startPlayingSong(songUri);
            });
            m_pausePoint = 0;
            m_pauseSongPercentagePassed = 0;
            return;
        }

        if(!isSongPlaying())
        {
            m_mediaPlayer.seekTo(m_pausePoint);
            m_mediaPlayer.start();
            return;
        }

        m_pausePoint = m_mediaPlayer.getCurrentPosition();
        m_pauseSongPercentagePassed = getSongPercentagePassedWhileSongActive();
        m_mediaPlayer.pause();
    }
    public void changeSongPlayingPoint(int songProgressPercentage)
    {
        if(!m_mediaPlayer.isPlaying())
        {
            for (SongPlayerServiceUiCallbacks uiCallbacks : m_uiCallbacksList)
                uiCallbacks.setSongPercentagePassed(m_pauseSongPercentagePassed);
            return;
        }

        int songMilliSecondsPassed = TimeConversions.songPercentageToMillisecondsPassed(songProgressPercentage, m_mediaPlayer.getDuration());
        m_mediaPlayer.pause();
        m_mediaPlayer.seekTo(songMilliSecondsPassed);
        m_mediaPlayer.start();

        for (SongPlayerServiceUiCallbacks uiCallbacks : m_uiCallbacksList)
            uiCallbacks.setSongPercentagePassed(songProgressPercentage);
    }








    // adding updating callbacks:
    public void addNewUiCallback(SongPlayerServiceUiCallbacks newUiCallback)
    {
        if(m_uiCallbacksList == null) m_uiCallbacksList = new ArrayList<>();
        m_uiCallbacksList.add(newUiCallback);
        forceUiUpdate();
    }
    public void resetUiCallbacks() { m_uiCallbacksList = null; }
    public void forceUiUpdate()
    {
        if(m_uiCallbacksList == null) return;

        for (SongPlayerServiceUiCallbacks uiCallbacks : m_uiCallbacksList)
        {
            onGetPicture(uiCallbacks);
            ongGetCurrDurationSong(uiCallbacks);
            onGetSongMaxDuration(uiCallbacks);
            uiCallbacks.setIsSongPlaying(m_mediaPlayer.isPlaying());
        }
    }







    // Playing songs helpers:
    private boolean isCurrSongPlayedForFirstTime() { return m_pausePoint == 0 && !m_mediaPlayer.isPlaying(); }
    private boolean isSongPlaying() { return m_mediaPlayer.isPlaying(); }
    private void startPlayingSong(Uri songUri)
    {
        MediaPlayer.OnPreparedListener playSongWithMediaPlayer = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer)
            {
                mediaPlayer.start();
                setupActionsOnSongEnded();
                forceUiUpdate();
            }
        };
        setActionsWithPreparedMediaPlayer(playSongWithMediaPlayer, songUri);
    }





    // Helpers:
    private void onGetPicture(SongPlayerServiceUiCallbacks uiCallbacks)
    {
        if(m_currSong == null) m_currSong = m_playlist.getNewSong();
        m_currSong.setOnRetrieveImageAction(imageUri -> uiCallbacks.setSongImage(imageUri));
    }
    private void ongGetCurrDurationSong(SongPlayerServiceUiCallbacks uiCallbacks)
    {
        String songDurationPassed = "";
        if(m_mediaPlayer.isPlaying()) songDurationPassed = TimeConversions.millisecondsToFormattedString(m_mediaPlayer.getCurrentPosition());
        else songDurationPassed = TimeConversions.millisecondsToFormattedString(m_pausePoint);

        uiCallbacks.setSongDurationPassed(songDurationPassed);
    }
    private void onGetSongMaxDuration(SongPlayerServiceUiCallbacks uiCallbacks)
    {
        if(m_mediaPlayer.isPlaying())
        {
            String songMaxDuration = "";
            songMaxDuration = TimeConversions.millisecondsToFormattedString(m_mediaPlayer.getDuration());
            uiCallbacks.setMaxDuration(songMaxDuration);
            return;
        }

        m_currSong.setOnRetrieveSongAction(songUri -> setOnGetSongMaxDurationWhenSongOffCallback(uiCallbacks, songUri));
    }
    private int getSongPercentagePassedWhileSongActive()
    {
        int songDurationPassedMilli = m_mediaPlayer.getCurrentPosition();
        int songMaxDurationMilli = m_mediaPlayer.getDuration();
        int songPercentagePassed = (int)(((float)songDurationPassedMilli / songMaxDurationMilli) * 100);

        return songPercentagePassed;
    }
    // The reason that is the song is off or on matters is because I can check song duration only when it is on:
    private void setOnGetSongMaxDurationWhenSongOffCallback(SongPlayerServiceUiCallbacks uiCallbacks, Uri songUri)
    {
        MediaPlayer.OnPreparedListener setSongDurationWithMediaPlayer = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer)
            {
                mediaPlayer.start();

                String formattedSongMaxDuration = TimeConversions.millisecondsToFormattedString(mediaPlayer.getDuration());
                uiCallbacks.setMaxDuration(formattedSongMaxDuration);

                mediaPlayer.pause();
            }
        };

        setActionsWithPreparedMediaPlayer(setSongDurationWithMediaPlayer, songUri);
    }
    private void setActionsWithPreparedMediaPlayer(MediaPlayer.OnPreparedListener actionsWithMp, Uri songUri)
    {
        try
        {
            m_mediaPlayer.reset();
            m_mediaPlayer.setDataSource(songUri.toString());
            m_mediaPlayer.setOnPreparedListener(actionsWithMp);
            m_mediaPlayer.prepareAsync();
        }
        catch (Exception e) { }
    }
    private void setSongPercentageAndTimePassed()
    {
        m_songTimeDalayHandler = new Handler();
        m_songCurrPointUpdater = new Runnable()
        {
            @Override
            public void run()
            {
                if (m_mediaPlayer.isPlaying())
                {
                    int songPercentagePassed = getSongPercentagePassedWhileSongActive();

                    String songDurationPassed = TimeConversions.millisecondsToFormattedString(m_mediaPlayer.getCurrentPosition());
                    for (SongPlayerServiceUiCallbacks uiCallbacks : m_uiCallbacksList)
                    {
                        uiCallbacks.setSongDurationPassed(songDurationPassed);
                        uiCallbacks.setSongPercentagePassed(songPercentagePassed);
                    }
                }
                m_songTimeDalayHandler.postDelayed(this, 1000);
            }
        };

        m_songTimeDalayHandler.postDelayed(m_songCurrPointUpdater, 1);
    }
    private void setupActionsOnSongEnded()
    {
        m_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                m_mediaPlayer = mediaPlayer;
                m_currSong = m_playlist.getNewSong();
                m_pausePoint = 0;
                m_pauseSongPercentagePassed = 0;


                Handler delayHandler = new Handler();
                Runnable delayToNotOverrideSongDurationRead = new Runnable()
                {
                    @Override
                    public void run() {playOrPause(); forceUiUpdate();}
                };
                delayHandler.postDelayed(delayToNotOverrideSongDurationRead, 500);
            }
        });
    }





    // Song Playing Attributes:
    private Playlist m_playlist;
    private Song m_currSong;
    private int m_pausePoint;
    private int m_pauseSongPercentagePassed;
    private MediaPlayer m_mediaPlayer;

    // UI attributes:
    private Handler m_songTimeDalayHandler;
    private Runnable m_songCurrPointUpdater;
    private ArrayList<SongPlayerServiceUiCallbacks> m_uiCallbacksList = null;





    // Simple helper functions
    private static class TimeConversions
    {
        public static int songPercentageToMillisecondsPassed(int songPercentagePassed, int songDurationMilliseconds)
        {
            float fractionOfSongPassed = ((float)songPercentagePassed / 100);
            int songDurationPassedMilliseconds = (int)(fractionOfSongPassed * songDurationMilliseconds);
            return songDurationPassedMilliseconds;
        }

        public static String millisecondsToFormattedString(int milliseconds)
        {
            int seconds = milliseconds / 1000;
            return String.format("%01d:%02d", (seconds / 60) % 60, seconds % 60);
        }
    }
    private static MediaPlayer setupMediaPlayer()
    {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(audioAttributes);

        return mediaPlayer;
    }
}
