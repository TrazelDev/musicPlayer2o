package com.example.musicplayer2o.UriElements.Songs;

import com.example.musicplayer2o.UriElements.OnRetrieveUriAction;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable
{
    public Playlist(ArrayList<Song> songs, int currIndex)
    {
        m_songList = songs;
        m_currIndex = currIndex;
    }
    public Song getNewSong()
    {
        Song retSong = m_songList.get(m_currIndex);
        m_currIndex++;
        m_currIndex %= m_songList.size();

        return retSong;
    }
    private ArrayList<Song> m_songList;
    private int m_currIndex;
}
