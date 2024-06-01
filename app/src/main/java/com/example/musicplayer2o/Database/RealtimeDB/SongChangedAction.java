package com.example.musicplayer2o.Database.RealtimeDB;

import com.example.musicplayer2o.UriElements.Songs.Song;

import java.util.ArrayList;

public interface SongChangedAction
{
    public void onSongsChanged(ArrayList<Song> songs);
}
