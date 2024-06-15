package com.example.musicplayer2o.UriElements.Songs.SongPlayer;

import android.net.Uri;

public interface SongPlayerUpdateCallbacks
{
    // Durations:
    public void setMaxDuration(String formattedMaxDuration);
    public void setSongElapsedDuration(String formattedSongDurationPassed);


    // image:
    public void setSongImage(Uri imageUri);


    public void setSongPercentagePassed(int songPercentagePassed);

    public void setIsSongPlaying(boolean isSongPlaying);
}
