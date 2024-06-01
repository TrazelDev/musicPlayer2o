package com.example.musicplayer2o.Database.RealtimeDB;

import com.example.musicplayer2o.UriElements.Songs.Song;
import com.example.musicplayer2o.UriElements.Songs.SongOwnership;

public abstract class RealtimeDB
{
    public static RealtimeDB getInstance()
    {
        if(m_instance == null) m_instance = FirebaseRealtimeDB.getFirebaseRealtimeDB();
        return m_instance;
    }

    public abstract void createNewUser();
    public abstract String generateUniqueID();
    public abstract void createNewSong(Song song);
    public abstract void loginUser();
    public abstract void registerSongToUser(SongOwnership ownershipType, Song song);


    // callbacks setting:
    public abstract void setupOnUsersSongsChangedCallback(UsersSongsChangedAction onUserSongsChanged);
    public abstract void removeUserSongsListener();
    public abstract void setupOnSongsChangedCallback(SongChangedAction onSongsChangedAction);
    public abstract void removeSongListener();

    private static RealtimeDB m_instance;
}
