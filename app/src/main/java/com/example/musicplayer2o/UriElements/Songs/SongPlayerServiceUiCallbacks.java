package com.example.musicplayer2o.UriElements.Songs;

import android.net.Uri;

public interface SongPlayerServiceUiCallbacks
{
    // Durations:
    public void setMaxDuration(String formattedMaxDuration);
    public void setSongDurationPassed(String formattedSongDurationPassed);


    // image:
    public void setSongImage(Uri imageUri);


    public void setSongPercentagePassed(int songPercentagePassed);

    public void setIsSongPlaying(boolean isSongPlaying);
}
