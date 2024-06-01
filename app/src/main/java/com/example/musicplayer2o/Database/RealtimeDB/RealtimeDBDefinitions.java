package com.example.musicplayer2o.Database.RealtimeDB;

import android.provider.ContactsContract;

public class RealtimeDBDefinitions
{
    public static final String DB_MAIN_URL = "https://musicplayer2o-default-rtdb.europe-west1.firebasedatabase.app/";


    public class User
    {
        public static final String FOLDER = "users";
        public static final String USER_SONGS = "songs";


        public static final String OWNED_SONGS = "users";
        public static final String REFERENCE_SONGS = "reference";
    }


    public class Song
    {
        public static final String FOLDER = "songs";


        public static final String NAME_KEY_ATTRIBUTE = "name";
        public static final String HAS_PICTURE_KEY_ATTRIBUTE = "hasPic";
    }
}
