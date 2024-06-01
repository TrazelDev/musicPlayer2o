package com.example.musicplayer2o.Database.RealtimeDB;


import com.example.musicplayer2o.Authentication.Authenticator;
import com.example.musicplayer2o.UriElements.Songs.Song;
import com.example.musicplayer2o.UriElements.Songs.SongOwnership;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseRealtimeDB extends RealtimeDB
{
    // Singleton functionality:
    public static FirebaseRealtimeDB getFirebaseRealtimeDB() { return new FirebaseRealtimeDB(); }
    private FirebaseRealtimeDB()
    {
        m_realTimeDB = FirebaseDatabase.getInstance(RealtimeDBDefinitions.DB_MAIN_URL);
        m_realTimeDBRef = m_realTimeDB.getReference("");
    }





    // Generating unique Ids for free use in the application:
    @Override
    public String generateUniqueID() { return m_realTimeDBRef.push().getKey(); }





    // User log in or register:
    @Override
    public void createNewUser()
    {
        m_userId = Authenticator.getInstance().getUserId();
        DatabaseReference userSongsReference = m_realTimeDBRef.child(RealtimeDBDefinitions.User.FOLDER)
                .child(m_userId).child(RealtimeDBDefinitions.User.USER_SONGS);

        userSongsReference.child(RealtimeDBDefinitions.User.OWNED_SONGS).setValue("null");
        userSongsReference.child(RealtimeDBDefinitions.User.REFERENCE_SONGS).setValue("null");
    }
    @Override
    public void loginUser() { m_userId = Authenticator.getInstance().getUserId(); }





    // uploading new songs:
    @Override
    public void createNewSong(Song song)
    {
        DatabaseReference newSongReference = m_realTimeDBRef.child(RealtimeDBDefinitions.Song.FOLDER).child(song.getSongId());

        newSongReference.child(RealtimeDBDefinitions.Song.NAME_KEY_ATTRIBUTE).setValue(song.getSongName());
        newSongReference.child(RealtimeDBDefinitions.Song.HAS_PICTURE_KEY_ATTRIBUTE).setValue(song.hasPicture());
    }
    @Override
    public void registerSongToUser(SongOwnership ownershipType, Song song)
    {
        String songOwnershipFolder = "";
        switch (ownershipType)
        {
            case OWNED:
                songOwnershipFolder = RealtimeDBDefinitions.User.OWNED_SONGS;
                break;
            case REFERENCE:
                songOwnershipFolder = RealtimeDBDefinitions.User.REFERENCE_SONGS;
                break;
        }

        m_realTimeDBRef.child(RealtimeDBDefinitions.User.FOLDER).child(m_userId).child(RealtimeDBDefinitions.User.USER_SONGS)
                .child(songOwnershipFolder).child(song.getSongId()).setValue(song.getSongName());
    }





    // Data changed callbacks:
    @Override
    public void setupOnUsersSongsChangedCallback(UsersSongsChangedAction onUserSongsChanged)
    {
        DatabaseReference userSongsReference = m_realTimeDBRef.child(RealtimeDBDefinitions.User.FOLDER).child(m_userId).child(RealtimeDBDefinitions.User.USER_SONGS);

        m_userSongsListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot usersSongs)
            {
                ArrayList<String> songIds = new ArrayList<>();
                uploadUserSongIdsToList(usersSongs.child(RealtimeDBDefinitions.User.OWNED_SONGS), songIds);
                uploadUserSongIdsToList(usersSongs.child(RealtimeDBDefinitions.User.REFERENCE_SONGS), songIds);
                onUserSongsChanged.onSongsChanged(songIds);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        };

        userSongsReference.addValueEventListener(m_userSongsListener);
    }
    @Override
    public void removeUserSongsListener()
    {
        DatabaseReference userSongsReference = m_realTimeDBRef.child(RealtimeDBDefinitions.User.FOLDER).child(m_userId).child(RealtimeDBDefinitions.User.USER_SONGS);
        if (userSongsReference != null && m_userSongsListener != null) userSongsReference.removeEventListener(m_userSongsListener);
    }
    @Override
    public void setupOnSongsChangedCallback(SongChangedAction onSongsChangedAction)
    {
        DatabaseReference allSongsReference = m_realTimeDBRef.child(RealtimeDBDefinitions.Song.FOLDER);

        m_allSongsListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot appSongs)
            {
                ArrayList<Song> songs = new ArrayList<>();
                uploadAppSongsToList(appSongs, songs);
                onSongsChangedAction.onSongsChanged(songs);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        };

        allSongsReference.addValueEventListener(m_allSongsListener);
    }
    @Override
    public void removeSongListener()
    {
        DatabaseReference allSongsReference = m_realTimeDBRef.child(RealtimeDBDefinitions.Song.FOLDER);
        if (allSongsReference != null && m_allSongsListener != null) allSongsReference.removeEventListener(m_allSongsListener);
    }





    // Auxiliary:
    private void uploadUserSongIdsToList(DataSnapshot dbSongs, ArrayList<String> songIds)
    {
        for(DataSnapshot songId : dbSongs.getChildren())
        {
            songIds.add(songId.getKey());
        }
    }
    private void uploadAppSongsToList(DataSnapshot dbSongs, ArrayList<Song> songs)
    {
        for(DataSnapshot song : dbSongs.getChildren())
        {
            try
            {
                songs.add(new Song(null, (String)song.child(RealtimeDBDefinitions.Song.NAME_KEY_ATTRIBUTE).getValue(),
                        (Boolean)song.child(RealtimeDBDefinitions.Song.HAS_PICTURE_KEY_ATTRIBUTE).getValue(), null, song.getKey()));
            }
            catch (Exception e) { }
        }
    }





    // Database information:
    private FirebaseDatabase m_realTimeDB;
    private DatabaseReference m_realTimeDBRef;
    private String m_userId;

    // Songs updated handlers:
    private ValueEventListener m_userSongsListener;
    private ValueEventListener m_allSongsListener;
}
