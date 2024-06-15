package com.example.musicplayer2o.UriElements.Songs;

import java.util.ArrayList;

public class Playlist
{
    public Playlist(ArrayList<Song> songs, int currIndex)
    {
        m_songList = songs;
        m_currSongIndex = currIndex;
    }
    public Song getCurrSong() { return m_songList.get(m_currSongIndex); }
    public void finishPlayingSong()
    {
        m_currSongIndex++;
        m_currSongIndex %= m_songList.size();
    }






    private ArrayList<Song> m_songList;
    private int m_currSongIndex;
}
