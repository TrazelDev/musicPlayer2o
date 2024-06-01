package com.example.musicplayer2o.Database.StorageDB;

import android.net.Uri;

import com.example.musicplayer2o.UriElements.OnRetrieveUriAction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseFilesStorage extends FilesStorage
{
    // Singleton functionality:
    private FirebaseFilesStorage() { m_storageReference = FirebaseStorage.getInstance().getReference(); }
    public static FirebaseFilesStorage getInstance() { return new FirebaseFilesStorage(); }






    // uploading files:
    @Override
    public void uploadSong(Uri songUri, String songName)
    {
        StorageReference songFile = m_storageReference
                .child(FilesStorageDefinitions.Song.FOLDER + songName + FilesStorageDefinitions.Song.EXTENSION);
        songFile.putFile(songUri);
    }
    @Override
    public void uploadPicture(Uri pictureUri, String pictureName)
    {
        StorageReference songFile = m_storageReference.child
                (FilesStorageDefinitions.Picture.FOLDER + pictureName + FilesStorageDefinitions.Picture.EXTENSION);
        songFile.putFile(pictureUri);
    }





    // accessing files:
    @Override
    public void setupOnRetrieveImageUriActions(OnRetrieveUriAction onRetrieveImageUri, String imageName)
    {
        m_storageReference.child(FilesStorageDefinitions.Picture.FOLDER)
                .child(imageName + FilesStorageDefinitions.Picture.EXTENSION).getDownloadUrl().addOnSuccessListener(
                new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri imageUri) { onRetrieveImageUri.actionWithUri(imageUri); }
                }
        );
    }

    @Override
    public void setupOnRetrieveSongUriActions(OnRetrieveUriAction onRetrieveSongUri, String songName)
    {
        m_storageReference.child(FilesStorageDefinitions.Song.FOLDER)
                .child(songName + FilesStorageDefinitions.Song.EXTENSION).getDownloadUrl().addOnSuccessListener(
                new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri songUri) { onRetrieveSongUri.actionWithUri(songUri); }
                }
        );
    }

    private StorageReference m_storageReference;




    public class FilesStorageDefinitions
    {
        public class Song
        {
            public static final String FOLDER = "songs/";
            public static final String EXTENSION = ".mp3";
        }

        public class Picture
        {
            public static final String FOLDER = "pics/";
            public static final String EXTENSION = ".jpg";
        }
    }
}
