package com.example.musicplayer2o.UriElements.Songs.SongPlayer;

public class TimeConversions
{
    public static String millisecondsToFormattedString(int milliseconds)
    {
        int seconds = milliseconds / 1000;
        return String.format("%01d:%02d", (seconds / 60) % 60, seconds % 60);
    }

    public static int convertToPercentages(int maxDuration, int elapsedDuration) { return (int)(((float)elapsedDuration / maxDuration) * 100); }

    public static int percentageToMillisecondsElapsed(int songPercentageElapsed, int songDurationMilliseconds)
    {
        float songFractionElapsed = (float)songPercentageElapsed / 100;
        int durationElapsedMilliseconds = (int)(songFractionElapsed * songDurationMilliseconds);

        return durationElapsedMilliseconds;
    }
}
