package com.example.musicplayer2o.UriElements.Songs.SongPlayer;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import com.example.musicplayer2o.UriElements.Songs.SongPlayerUpdateCallbacks;

import java.io.IOException;

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
    public void playOrPause()
    {
        if(!m_mediaPlayer.isAssetLoaded())
        {
            m_playlist.getCurrSong().setOnRetrieveSongAction((song) -> m_mediaPlayer.loadAndPlayAsset(song));
            return;
        }


        if(m_mediaPlayer.isPlaying()) m_mediaPlayer.pauseAsset();
        else m_mediaPlayer.resumePlaying();
    }

    @Override
    public void seekTo() {

    }

    @Override
    public void addUpdatingCallbacks(SongPlayerUpdateCallbacks updateCallbacks) {

    }

    @Override
    public void forceAllUpdates() {

    }










    // helping functions:
    private static class AdvancedMediaPlayer1
    {
        public static void setupMediaPlayer()
        {
            if(s_mediaPlayer != null) return;

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            s_mediaPlayer = new MediaPlayer();
            s_mediaPlayer.setAudioAttributes(audioAttributes);
        }

        public static boolean isSongLoaded() { return s_mediaPlayer.getDuration() > 0; }
        public static boolean isSongPlaying() { return s_mediaPlayer.isPlaying(); }
        public static void loadAndPlaySong(Uri song)
        {
            try
            {
                s_mediaPlayer.reset();
                s_mediaPlayer.setDataSource(song.toString());
                s_mediaPlayer.prepareAsync();
                s_mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { public void onPrepared(MediaPlayer mediaPlayer) { mediaPlayer.start(); } });
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        public static void startPlayingSong(MediaPlayer md, Uri song, int playingPointMillisecond)
        {

        }
        public static void pauseSong()
        {
            // s_pausePointMilliseconds =
        }





        // private static void setActionsWithMediaPlayer()
        private static MediaPlayer s_mediaPlayer;
        private static int s_pausePointMilliseconds;
    }



    AdvancedMediaPlayer m_mediaPlayer;
}
