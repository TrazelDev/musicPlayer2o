package com.example.musicplayer2o.UriElements.Songs.SongPlayer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.musicplayer2o.UriElements.Songs.Playlist;

import java.util.ArrayList;

public abstract class AbstractSongPlayerService extends Service
{
    // supported function that will be implemented inside of the child class
    public abstract void playOrPause();
    public abstract void seekTo(int songPercentage);
    public abstract void forceUpdates();





    // basic utility functions
    public void setPlaylist(Playlist playlist) { m_playlist = playlist; }
    public void addUpdatingCallbacks(SongPlayerUpdateCallbacks updateCallbacks) { m_updateCallbacksList.add(updateCallbacks); }
    public void clearUpdatingCallbacks() { m_updateCallbacksList = new ArrayList<>(); }









    // Binder related ( Used to access the service in activities and fragments ):
    @Override
    public IBinder onBind(Intent intent) { return binder; }
    private final IBinder binder = new AbstractSongPlayerService.LocalBinder();
    public class LocalBinder extends Binder { public AbstractSongPlayerService getService() { return AbstractSongPlayerService.this; } }





    protected Playlist m_playlist;
    protected ArrayList<SongPlayerUpdateCallbacks> m_updateCallbacksList = new ArrayList<>();
}
