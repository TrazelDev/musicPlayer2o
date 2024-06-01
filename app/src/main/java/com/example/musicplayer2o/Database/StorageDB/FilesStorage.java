package com.example.musicplayer2o.Database.StorageDB;


import android.net.Uri;

import com.example.musicplayer2o.UriElements.OnRetrieveUriAction;

public abstract class FilesStorage
{
    // Singleton functionality:
    private static FilesStorage m_instance;
    public static FilesStorage getInstance()
    {
        if(m_instance == null) m_instance = FirebaseFilesStorage.getInstance();
        return m_instance;
    }





    // uploading files:
    public abstract void uploadSong(Uri songUri, String songName);
    public abstract void uploadPicture(Uri pictureUri, String pictureName);






    // accessing files:
    public abstract void setupOnRetrieveImageUriActions(OnRetrieveUriAction onRetrieveImageUri, String imageName);
    public abstract void setupOnRetrieveSongUriActions(OnRetrieveUriAction onRetrieveSongUri, String songName);
}
