package com.example.musicplayer2o.UriElements.Songs;

import java.util.ArrayList;

public class Playlist
{
    public Playlist(ArrayList<Song> songs, int currIndex)
    {
        m_songList = songs;
        m_currSongIndex = currIndex;
        m_isCurrSongPlaying = false;
        m_firstTimeGettingSong = true;
    }
    public Song getCurrSong() { return m_songList.get(m_currSongIndex); }
    public Song finishPlayingSong()
    {
        if(m_firstTimeGettingSong) m_firstTimeGettingSong = false;
        else m_currSongIndex = getNextSongIndex(m_currSongIndex, m_songList.size());
        Song newSong = m_songList.get(m_currSongIndex);

        return newSong;
    }
    public void setSongIsPlaying() { m_isCurrSongPlaying = true; }
    public void setSongIsPaused() { m_isCurrSongPlaying = false; }
    public boolean isSongPlaying() { return m_isCurrSongPlaying; }





    private static int getNextSongIndex(int currSongIndex, int playlistSongCount)
    {
        currSongIndex++;
        currSongIndex %= playlistSongCount;

        return currSongIndex;
    }
    private ArrayList<Song> m_songList;
    private int m_currSongIndex;
    private boolean m_isCurrSongPlaying;
    private boolean m_firstTimeGettingSong;
}
