package com.example.musicplayer2o.UriElements.Songs.SongPlayer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.musicplayer2o.UriElements.Songs.Playlist;
import com.example.musicplayer2o.UriElements.Songs.SongPlayerUpdateCallbacks;

import java.util.ArrayList;

public abstract class AbstractSongPlayerService extends Service
{
    public void setPlaylist(Playlist playlist) { m_playlist = playlist; }
    public abstract void playOrPause();
    public abstract void seekTo();
    public abstract void addUpdatingCallbacks(SongPlayerUpdateCallbacks updateCallbacks);
    public abstract void forceAllUpdates();



    // Binder related ( Used to access the service in activities and fragments ):
    @Override
    public IBinder onBind(Intent intent) { return binder; }
    private final IBinder binder = new AbstractSongPlayerService.LocalBinder();
    public class LocalBinder extends Binder { public AbstractSongPlayerService getService() { return AbstractSongPlayerService.this; } }









    protected Playlist m_playlist;
    protected ArrayList<SongPlayerUpdateCallbacks> m_uiCallbacksList = new ArrayList<>();
}
