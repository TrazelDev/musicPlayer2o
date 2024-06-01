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
    public static FirebaseRealtimeDB getFirebaseRealtimeDB() { return new FirebaseRealtimeDB(); }
    private FirebaseRealtimeDB()
    {
        m_realTimeDB = FirebaseDatabase.getInstance("https://musicplayer2o-default-rtdb.europe-west1.firebasedatabase.app/");
        m_realTimeDBRef = m_realTimeDB.getReference("");
    }

    @Override
    public void createNewUser()
    {
        m_userId = Authenticator.getInstance().getUserId();
        DatabaseReference userSongsReference = m_realTimeDBRef.child(USERS_FOLDER).child(m_userId).child(USERS_SONGS);

        userSongsReference.child(USERS_OWNED_SONGS).setValue("null");
        userSongsReference.child(REFERENCE_SONGS).setValue("null");
    }
    @Override
    public void loginUser() { m_userId = Authenticator.getInstance().getUserId(); }
    @Override
    public String generateUniqueID()
    {
        return m_realTimeDBRef.push().getKey();
    }

    @Override
    public void createNewSong(Song song)
    {
        DatabaseReference newSongReference = m_realTimeDBRef.child(ALL_SONGS).child(song.getSongId());

        newSongReference.child(SONG_NAME).setValue(song.getSongName());
        newSongReference.child(SONG_HAS_PIC).setValue(song.hasPicture());
    }

    @Override
    public void registerSongToUser(SongOwnership ownershipType, Song song)
    {
        String songFolder = "";
        switch (ownershipType)
        {
            case OWNED:
                songFolder = USERS_OWNED_SONGS;
                break;
            case REFERENCE:
                songFolder = REFERENCE_SONGS;
                break;
        }

        m_realTimeDBRef.child(USERS_FOLDER).child(m_userId).child(USERS_SONGS).child(songFolder).child(song.getSongId()).setValue(song.getSongName());
    }

    @Override
    public void setupOnUsersSongsChangedCallback(UsersSongsChangedAction onUserSongsChanged)
    {
        DatabaseReference userSongsReference = m_realTimeDBRef.child(USERS_FOLDER).child(m_userId).child(USERS_SONGS);

        m_userSongsListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot usersSongs)
            {
                ArrayList<String> songIds = new ArrayList<>();
                uploadUserSongsToList(usersSongs.child(USERS_OWNED_SONGS), songIds);
                uploadUserSongsToList(usersSongs.child(REFERENCE_SONGS), songIds);
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
        DatabaseReference userSongsReference = m_realTimeDBRef.child("users").child(m_userId).child(USERS_SONGS);
        if (userSongsReference != null && m_userSongsListener != null) userSongsReference.removeEventListener(m_userSongsListener);
    }
    @Override
    public void setupOnSongsChangedCallback(SongChangedAction onSongsChangedAction)
    {
        DatabaseReference allSongsReference = m_realTimeDBRef.child(ALL_SONGS);

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
        DatabaseReference allSongsReference = m_realTimeDBRef.child(ALL_SONGS);
        if (allSongsReference != null && m_allSongsListener != null) allSongsReference.removeEventListener(m_allSongsListener);
    }

    // Auxiliary:
    private void uploadUserSongsToList(DataSnapshot dbSongs, ArrayList<String> songIds)
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
                songs.add(new Song(null, (String)song.child(SONG_NAME).getValue(), (Boolean)song.child(SONG_HAS_PIC).getValue(), null, song.getKey()));
            }
            catch (Exception e) { }
        }
    }

    // users accesses:
    public static final String USERS_FOLDER = "users";
    public static final String USERS_SONGS = "songs";
    public static final String USERS_OWNED_SONGS = "users";
    public static final String REFERENCE_SONGS = "reference";

    // song accesses:
    public static final String ALL_SONGS = "songs";
    public static final String SONG_NAME = "name";
    public static final String SONG_HAS_PIC = "hasPic";

    private FirebaseDatabase m_realTimeDB;
    private DatabaseReference m_realTimeDBRef;
    private String m_userId;

    private ValueEventListener m_userSongsListener;
    private ValueEventListener m_allSongsListener;
}
