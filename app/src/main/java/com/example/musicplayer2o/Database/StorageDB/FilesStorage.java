package com.example.musicplayer2o.Database.StorageDB;


import android.net.Uri;

import com.example.musicplayer2o.UriElements.OnRetrieveUriAction;
import com.example.musicplayer2o.UriElements.Songs.Song;

public abstract class FilesStorage
{
    public static FilesStorage getInstance()
    {
        if(m_instance == null) m_instance = FirebaseFilesStorage.getInstance();
        return m_instance;
    }

    public abstract void uploadSong(Uri songUri, String songName);

    public abstract void uploadPicture(Uri pictureUri, String pictureName);
    public abstract void getImageUri(OnRetrieveUriAction onRetrieveImageUri, String imageName);
    public abstract void getSongUri(OnRetrieveUriAction onRetrieveSongUri, String songName);

    private static FilesStorage m_instance;
}
