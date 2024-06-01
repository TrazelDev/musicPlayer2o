package com.example.musicplayer2o.App.playSongs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.musicplayer2o.Database.RealtimeDB.RealtimeDB;
import com.example.musicplayer2o.R;
import com.example.musicplayer2o.UriElements.Songs.GeneralSongListAdapter;
import com.example.musicplayer2o.UriElements.Songs.Playlist;
import com.example.musicplayer2o.UriElements.Songs.Song;

import java.util.ArrayList;

public class SongPlayListFragment extends Fragment
{
    public interface PlayListToSongPlayingTransitionCallback { public void execute(Playlist playlist); }
    public SongPlayListFragment(PlayListToSongPlayingTransitionCallback transitionToPlayingSongCallback)
    {
        m_transitionToPlayingSongCallback = transitionToPlayingSongCallback;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_song_play_list, container, false);

        setupViewsById(view);
        setupSongListAdapter();
        setupUserSongList();

        return view;
    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        destroySongDataListeners();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        setupUserSongList();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        destroySongDataListeners();
    }

    private void setupViewsById(View view) { m_listView = view.findViewById(R.id.songPlayListView); }

    private void setupUserSongList()
    {
        m_userSongsIds = new ArrayList<>();
        m_allAppSongs = new ArrayList<>();

        RealtimeDB.getInstance().setupOnUsersSongsChangedCallback(
                userSongsIds ->
                {
                    m_userSongsIds = userSongsIds;
                    m_userSongs = Song.generateUserSongList(m_allAppSongs, m_userSongsIds);
                    m_adapter.updateData(m_userSongs);
                }
        );

        RealtimeDB.getInstance().setupOnSongsChangedCallback(
                appSongs ->
                {
                    m_allAppSongs = appSongs;
                    m_userSongs = Song.generateUserSongList(m_allAppSongs, m_userSongsIds);
                    m_adapter.updateData(m_userSongs);
                }
        );
    }

    private void setupSongListAdapter()
    {
        m_userSongs = new ArrayList<>();
        m_adapter = new GeneralSongListAdapter(requireContext(), m_userSongs, NO_INSTRUCTION, R.drawable.icon_play,
                song -> { song.setOnRetrieveSongAction(uri -> { }); }); // this step is for the songs to be loaded so we do not have to wait later

        m_listView.setAdapter(m_adapter);
        m_adapter.uploadOnSongActionListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int position = m_listView.getPositionForView(view);
                if (position == ListView.INVALID_POSITION) return;


                Playlist playlist = new Playlist(m_adapter.getSongs(), position);
                m_transitionToPlayingSongCallback.execute(playlist);
            }
        });
    }

    // this is used so a certain code will not run while the fragment is paused and it will crush the program
    private void destroySongDataListeners()
    {
        RealtimeDB.getInstance().removeUserSongsListener();
        RealtimeDB.getInstance().removeSongListener();
    }
    private final String NO_INSTRUCTION = "";
    private GeneralSongListAdapter m_adapter;
    private ListView m_listView;
    private ArrayList<Song> m_userSongs;
    private ArrayList<String> m_userSongsIds;
    private ArrayList<Song> m_allAppSongs;
    private PlayListToSongPlayingTransitionCallback m_transitionToPlayingSongCallback;
}