package com.example.musicplayer2o.Database.StorageDB;

import android.net.Uri;

import com.example.musicplayer2o.UriElements.OnRetrieveUriAction;
import com.example.musicplayer2o.UriElements.Songs.Song;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseFilesStorage extends FilesStorage
{
    private FirebaseFilesStorage() { m_storageReference = FirebaseStorage.getInstance().getReference(); }
    public static FirebaseFilesStorage getInstance() { return new FirebaseFilesStorage(); }

    @Override
    public void uploadSong(Uri songUri, String songName)
    {
        StorageReference songFile = m_storageReference.child(SONGS_FOLDER + songName + SONG_EXTENSION);
        songFile.putFile(songUri);
    }

    @Override
    public void uploadPicture(Uri pictureUri, String pictureName)
    {
        StorageReference songFile = m_storageReference.child(PICTURES_FOLDER + pictureName + PICTURE_EXTENSION);
        songFile.putFile(pictureUri);
    }

    @Override
    public void getImageUri(OnRetrieveUriAction onRetrieveImageUri, String imageName)
    {
        m_storageReference.child(PICTURES_FOLDER).child(imageName + PICTURE_EXTENSION).getDownloadUrl().addOnSuccessListener(
                new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri imageUri) { onRetrieveImageUri.actionWithUri(imageUri); }
                }
        );
    }

    @Override
    public void getSongUri(OnRetrieveUriAction onRetrieveSongUri, String songName)
    {
        m_storageReference.child(SONGS_FOLDER).child(songName + SONG_EXTENSION).getDownloadUrl().addOnSuccessListener(
                new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri songUri) { onRetrieveSongUri.actionWithUri(songUri); }
                }
        );
    }

    public static final String SONGS_FOLDER = "songs/";

    public static final String PICTURES_FOLDER = "pics/";
    public static final String SONG_EXTENSION = ".mp3";
    public static final String PICTURE_EXTENSION = ".jpg";
    private StorageReference m_storageReference;
}
