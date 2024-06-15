package com.example.musicplayer2o.UriElements.Songs.SongPlayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import com.example.musicplayer2o.UriElements.Songs.Playlist;

public class MediaSongPlayerService extends AbstractSongPlayerService
{
    @Override
    public IBinder onBind(Intent intent) { return super.onBind(intent); }
    public void onCreate()
    {
        super.onCreate();
        m_mediaPlayer = new AdvancedMediaPlayer();
    }
    @Override
    public boolean onUnbind(Intent intent)
    {
        m_mediaPlayer.release();
        m_mediaPlayer = null;
        return super.onUnbind(intent);
    }
    /** the reason the functions is overridden in this class is because the song can start playing only once the the playlist is loaded because
     * before that there is no access to the songs */
    @Override
    public void setPlaylist(Playlist playlist)
    {
        super.setPlaylist(playlist);
        forceUpdates();
        setupLiveCallbackUpdater();
    }





    // utility functions that the service supports:
    @Override
    public void playOrPause()
    {
        MediaPlayer.OnPreparedListener playSong = new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer)
            {
                mediaPlayer.start();
                updateAllCallbacks();
                setupSongEndedActions();
            }
        };

        if(!m_mediaPlayer.isAssetLoaded()) m_playlist.getCurrSong().setOnRetrieveSongAction( song -> { m_mediaPlayer.loadAsset(song, playSong); });
        else if(m_mediaPlayer.isPlaying()) m_mediaPlayer.pauseAsset();
        else m_mediaPlayer.resumePlaying();
    }

    @Override
    public void seekTo(int songPercentage)
    {
        if(!m_mediaPlayer.isPlaying()) return;

        int maxDurationMilliseconds = m_mediaPlayer.getAssetMaxMilliseconds();
        int songMillisecondsElapsed = TimeConversions.percentageToMillisecondsElapsed(songPercentage, maxDurationMilliseconds);

        m_mediaPlayer.seekTo(songMillisecondsElapsed);
    }
    @Override
    public void forceUpdates()
    {
        if(m_playlist == null) return;

        MediaPlayer.OnPreparedListener updateActions = new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer)
            {
                updateAllCallbacks();
                setupSongEndedActions();
            }
        };

        if(!m_mediaPlayer.isAssetLoaded()) m_playlist.getCurrSong().setOnRetrieveSongAction(song -> m_mediaPlayer.loadAsset(song, updateActions));
        else updateActions.onPrepared(m_mediaPlayer);
    }


    private void setupLiveCallbackUpdater()
    {
        m_callbackUpdateRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                m_songUpdateCallbacksHandler.postDelayed(this, 500);
                if(m_mediaPlayer == null) return;

                updateAllCallbacks();
            }
        };

        m_songUpdateCallbacksHandler = new Handler();
        m_songUpdateCallbacksHandler.postDelayed(m_callbackUpdateRunnable, 1);
    }

    private void updateAllCallbacks()
    {
        if(m_playlist == null) return;

        int songMaxDurationMilliseconds = m_mediaPlayer.getAssetMaxMilliseconds();
        int songElapsedDurationMilliseconds = m_mediaPlayer.getAssetElapsedMilliseconds();
        int songPercentageElapsed = TimeConversions.convertToPercentages(songMaxDurationMilliseconds, songElapsedDurationMilliseconds);

        String formattedSongMaxDuration = TimeConversions.millisecondsToFormattedString(songMaxDurationMilliseconds);
        String formattedSongElapsedDuration = TimeConversions.millisecondsToFormattedString(songElapsedDurationMilliseconds);

        for(SongPlayerUpdateCallbacks updateCallbacks : m_updateCallbacksList)
        {
            // Duration based:
            updateCallbacks.setMaxDuration(formattedSongMaxDuration);
            updateCallbacks.setSongPercentagePassed(songPercentageElapsed);
            updateCallbacks.setSongElapsedDuration(formattedSongElapsedDuration);


            updateCallbacks.setIsSongPlaying(m_mediaPlayer.isPlaying());
            m_playlist.getCurrSong().setOnRetrieveImageAction(image -> updateCallbacks.setSongImage(image));
        }
    }





    protected AdvancedMediaPlayer m_mediaPlayer;
    protected Handler m_songUpdateCallbacksHandler;
    private Runnable m_callbackUpdateRunnable;



    private void setupSongEndedActions()
    {
        m_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                m_mediaPlayer = new AdvancedMediaPlayer();
                m_playlist.finishPlayingSong();


                forceUpdates();
                playOrPause();
            }
        });
    }
}
