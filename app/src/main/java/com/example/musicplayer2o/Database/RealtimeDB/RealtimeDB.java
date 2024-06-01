package com.example.musicplayer2o.Database.RealtimeDB;

import com.example.musicplayer2o.UriElements.Songs.Song;
import com.example.musicplayer2o.UriElements.Songs.SongOwnership;

public abstract class RealtimeDB
{
    // Singleton functionality:
    private static RealtimeDB m_instance;
    public static RealtimeDB getInstance()
    {
        if(m_instance == null) m_instance = FirebaseRealtimeDB.getFirebaseRealtimeDB();
        return m_instance;
    }





    // Generating unique Ids for free use in the application:
    public abstract String generateUniqueID();





    // User log in or register:
    public abstract void createNewUser();
    public abstract void loginUser();





    // uploading new songs:
    public abstract void createNewSong(Song song);
    public abstract void registerSongToUser(SongOwnership ownershipType, Song song);






    // Data changed callbacks:
    public abstract void setupOnUsersSongsChangedCallback(UsersSongsChangedAction onUserSongsChanged);
    public abstract void removeUserSongsListener();
    public abstract void setupOnSongsChangedCallback(SongChangedAction onSongsChangedAction);
    public abstract void removeSongListener();
}
