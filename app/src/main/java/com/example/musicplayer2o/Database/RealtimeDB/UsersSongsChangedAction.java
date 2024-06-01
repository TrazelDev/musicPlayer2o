package com.example.musicplayer2o.Database.RealtimeDB;

import java.util.ArrayList;

public interface UsersSongsChangedAction
{
    public void onSongsChanged(ArrayList<String> songId);
}
