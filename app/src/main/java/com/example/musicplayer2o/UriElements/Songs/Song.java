package com.example.musicplayer2o.UriElements.Songs;

import android.net.Uri;

import com.example.musicplayer2o.Database.RealtimeDB.RealtimeDB;
import com.example.musicplayer2o.Database.StorageDB.FilesStorage;
import com.example.musicplayer2o.UriElements.OnRetrieveUriAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Song
{
    // Generation of specific song lists:
    public static ArrayList<Song> generateNonUserSongList(ArrayList<Song> appSongs, ArrayList<String> userSongIds)
    {
        ArrayList<Song> appNonUserSongs = new ArrayList<>();
        HashSet<String> userSongIdsSet = new HashSet<>(userSongIds);

        for(Song song : appSongs)
            if(!userSongIdsSet.contains(song.getSongId())) appNonUserSongs.add(song);

        return appNonUserSongs;
    }
    public static ArrayList<Song> generateUserSongList(ArrayList<Song> appSongs, ArrayList<String> userSongIds)
    {
        ArrayList<Song> userSongs = new ArrayList<>();
        HashSet<String> userSongIdsSet = new HashSet<>(userSongIds);

        for(Song song : appSongs)
            if(userSongIdsSet.contains(song.getSongId())) userSongs.add(song);

        return userSongs;
    }





    // Construction:
    public Song(Uri songUri, String songName, boolean hasPicture, Uri imageUri)
    {
        m_songUri = songUri;
        m_imageUri = imageUri;
        m_songName = songName;
        m_hasPicture = hasPicture;

        m_songUniqueID = RealtimeDB.getInstance().generateUniqueID();
    }
    public Song(Uri songUri, String songName, boolean hasPicture, Uri imageUri, String songUniqueID)
    {
        m_songUri = songUri;
        m_imageUri = imageUri;
        m_songName = songName;
        m_hasPicture = hasPicture;

        m_songUniqueID = songUniqueID;
    }




    // public utility methods:
    public void uploadSong()
    {
        RealtimeDB.getInstance().createNewSong(this);
        RealtimeDB.getInstance().registerSongToUser(SongOwnership.OWNED,this);


        FilesStorage.getInstance().uploadSong(m_songUri, m_songUniqueID);
        if(m_hasPicture) FilesStorage.getInstance().uploadPicture(m_imageUri, m_songUniqueID);
    }
    public void registerReferenceSongToUser() { RealtimeDB.getInstance().registerSongToUser(SongOwnership.REFERENCE,this); }





    // getters & setters:
    public String getSongId() { return m_songUniqueID; }
    public String getSongName() { return m_songName; }
    public boolean hasPicture() { return m_hasPicture; }
    public void setOnRetrieveImageAction(OnRetrieveUriAction onRetrieveImageUri)
    {
        if(m_imageUri != null || !m_hasPicture)
        {
            onRetrieveImageUri.actionWithUri(m_imageUri);
            return;
        }

        FilesStorage.getInstance().setupOnRetrieveImageUriActions(imageUri ->
            {
                m_imageUri = imageUri;
                onRetrieveImageUri.actionWithUri(imageUri);
            }, m_songUniqueID);
    }

    public void setOnRetrieveSongAction(OnRetrieveUriAction onRetrieveSongUri)
    {
        if(m_songUri != null)
        {
            onRetrieveSongUri.actionWithUri(m_songUri);
            return;
        }

        FilesStorage.getInstance().setupOnRetrieveSongUriActions(songUri ->
        {
            m_songUri = songUri;
            onRetrieveSongUri.actionWithUri(songUri);
        }, m_songUniqueID);
    }




    // Steaming and download properties:
    private Uri m_songUri;
    private Uri m_imageUri;

    // Basic properties:
    private String m_songName;
    private boolean m_hasPicture;
    private String m_songUniqueID;
}
